package com.resChk.service;

import com.resChk.client.GeminiAiClient;
import com.resChk.dto.AnalysisResponseDTO;
import com.resChk.serviceClient.ResumeService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Service
public class ResumeServiceImpl implements ResumeService {
    private static final Logger logger = LoggerFactory.getLogger(ResumeServiceImpl.class);

    private final GeminiAiClient aiClient;

    public ResumeServiceImpl(GeminiAiClient aiClient) {
        this.aiClient = aiClient;
        logger.info("ResumeServiceImpl initialized.");
    }

    @Override
    public AnalysisResponseDTO analyzeResume(MultipartFile file) throws Exception{
        logger.info("ResumeService: Starting analysis for file: {}", file.getOriginalFilename());

        String extractedText;
        try{
            extractedText = extractTextFromFile(file);
            if (extractedText.length() > 200) {
                logger.debug("Extracted text from {}: \n{}...", file.getOriginalFilename(), extractedText.substring(0, 200));
            } else {
                logger.debug("Extracted text from {}: \n{}", file.getOriginalFilename(), extractedText);
            }
        }catch (IOException e){
            logger.error("Error extracting text from file: {}", file.getOriginalFilename(), e);
            throw new Exception("Failed to read content from the resume file.", e);
        }

        if (extractedText.trim().isEmpty()) {
            logger.warn("Extracted text is empty for file: {}", file.getOriginalFilename());
            return new AnalysisResponseDTO("Could not extract any text from the resume. Please ensure it's not an image-only file or empty.", null);
        }

        String prompt = "Please analyze the following resume text and provide a concise summary of strengths, " +
                "areas for improvement, and overall suitability for a software engineering role. " +
                "Focus on skills, experience, and project work. Format your response clearly.\n\nResume Text:\n" + extractedText;

        String extractedTextPreview = extractedText.substring(0, Math.min(extractedText.length(), 200)) + "...";
        try{
            logger.info("Sending prompt to Gemini for file: {}", file.getOriginalFilename());
            String analysisFromApi = aiClient.generateContent(prompt);
            logger.info("Result -\n{}", analysisFromApi);

            return new AnalysisResponseDTO(analysisFromApi, extractedTextPreview);
        }catch (RuntimeException e) {
            logger.error("Error during AI analysis for file {}: {}", file.getOriginalFilename(), e.getMessage(), e);
            // Return a DTO indicating the AI error
            return new AnalysisResponseDTO("An error occurred during AI analysis: " + e.getMessage(),
                    extractedTextPreview);
        }
    }

    private String extractTextFromFile(MultipartFile file) throws IOException {
        String contentType = file.getContentType();
        InputStream inputStream = new ByteArrayInputStream(file.getBytes());

        if (contentType == null) {
            throw new IOException("Could not determine file content type.");
        }

        switch (contentType){
            case "application/pdf":
                logger.debug("Extracting text from PDF file");
                try (PDDocument document = PDDocument.load(inputStream)){
                    PDFTextStripper stripper = new PDFTextStripper();
                    return stripper.getText(document);
                }
            case "text/plain":
                logger.debug("Extracting text from TXT file");
                return new String(file.getBytes(), StandardCharsets.UTF_8);
            case "application/vnd.openxmlformats-officedocument.wordprocessingml.document":
                logger.debug("Extracting text from docx file");
                try (XWPFDocument document = new XWPFDocument(inputStream)){
                    XWPFWordExtractor extractor = new XWPFWordExtractor(document);
                    return extractor.getText();
                }
            default:
                logger.warn("Unsupported content type for text extraction: {}", contentType);
                throw new IOException("Unsupported file type for text extraction: " + contentType);
        }
    }
}
