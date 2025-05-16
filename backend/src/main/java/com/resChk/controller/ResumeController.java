package com.resChk.controller;

import com.resChk.serviceClient.ResumeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/")
@CrossOrigin(origins = "http://localhost:5173")
public class ResumeController {
    private static final Logger logger = LoggerFactory.getLogger(ResumeController.class);

    private final ResumeService service;

    @Autowired
    public ResumeController(ResumeService service) {
        this.service = service;
    }

    @PostMapping("check-resume")
    public ResponseEntity<?> checkResume(@RequestParam("resumeFile")MultipartFile file){
        logger.info("Received request to check resume. File name: {}", file.getOriginalFilename());

        if(file.isEmpty()){
            logger.warn("Received file is empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please select a file to upload.");
        }

        String contentType = file.getContentType();
        if(contentType == null ||
                !(contentType.equals("application/pdf") || contentType.equals("text/plain") ||
                        contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))){ // DOCX

            logger.warn("Invalid file type received: {}", contentType);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid file type. Please upload a PDF, DOCX, or TXT file.");
        }

        try{
            String analysisResult = service.analyzeResume(file);
            logger.info("Resume analysis successful for file: {}", file.getOriginalFilename());
            return ResponseEntity.ok(analysisResult);
        }catch (Exception e){
            logger.error("Error processing file: {}", file.getOriginalFilename(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while processing your resume: " + e.getMessage());
        }
    }
}
