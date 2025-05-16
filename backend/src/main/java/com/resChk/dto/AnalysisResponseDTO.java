package com.resChk.dto;

public class AnalysisResponseDTO {
    private String message;
    private String extractedTextPreview;

    public AnalysisResponseDTO() {
    }

    public AnalysisResponseDTO(String message, String extractedTextPreview) {
        this.message = message;
        this.extractedTextPreview = extractedTextPreview;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getExtractedTextPreview() {
        return extractedTextPreview;
    }

    public void setExtractedTextPreview(String extractedTextPreview) {
        this.extractedTextPreview = extractedTextPreview;
    }
}
