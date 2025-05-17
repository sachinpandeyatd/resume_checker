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

        String prompt = String.format(
                "You are an expert resume reviewer and career coach. " +
                "Analyze the following resume text for a software engineering role. " +
                "Provide a detailed, constructive, and professional analysis. " +
                "Please structure your response in Markdown format with the following sections exactly as titled:\n\n" +
                "### Strengths\n" +
                "[Provide a bulleted list of key strengths. Be specific and highlight transferable skills, technical proficiencies, and notable achievements. Each point should be well-explained.]\n\n" +
                "### Areas for Improvement\n" +
                "[Provide a bulleted list of specific areas where the resume can be improved. Offer actionable advice. For example, instead of saying 'Project detail is lacking,' suggest 'Expand on the project descriptions by including specific technologies used, challenges faced, and solutions implemented. Consider adding GitHub links if available.' Focus on clarity, impact, and completeness.]\n\n" +
                "### Overall Suitability\n" +
                "[Give an overall assessment of the candidate's suitability for a generic software engineering role based *only* on the provided resume text. Mention the types of roles they might be a good fit for. Conclude with 1-2 key recommendations for the candidate to enhance their resume's effectiveness.]\n\n" +
                "--- RESUME TEXT BEGIN ---\n" +
                "%s\n" +  // Placeholder for the extracted resume text
                "--- RESUME TEXT END ---\n\n" +
                "Ensure your analysis is based SOLELY on the provided text and avoids making assumptions not supported by the resume.",
                extractedText
        );

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
