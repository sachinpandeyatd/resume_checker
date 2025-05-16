package com.resChk.client;

import com.resChk.dto.GeminiRequestDTO;
import com.resChk.dto.GeminiResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class GeminiAiClient {
    private static final Logger logger = LoggerFactory.getLogger(GeminiAiClient.class);

    private final RestTemplate restTemplate;

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    public GeminiAiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String generateContent(String promptText){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        GeminiRequestDTO request = new GeminiRequestDTO(promptText);
        HttpEntity<GeminiRequestDTO> entity = new HttpEntity<>(request, headers);

        String fullApiUrl = apiUrl + "?key=" + apiKey;
        logger.info("Calling Gemini API: {}", fullApiUrl);

        try {
            GeminiResponseDTO response = restTemplate.postForObject(fullApiUrl, entity, GeminiResponseDTO.class);

            if (response != null && response.getFirstCandidateText() != null) {
                logger.info("Successfully received response from Gemini API.");
                return response.getFirstCandidateText();
            } else {
                logger.warn("Received null or empty response from Gemini API. Response: {}", response);
                // You might want to inspect the full 'response' object here if it's not null but text is missing
                return "Could not get a valid response from the AI. Please try again.";
            }
        }catch (HttpClientErrorException e) {
            logger.error("Error calling Gemini API: {} - {} \nResponse Body: {}", e.getStatusCode(), e.getMessage(), e.getResponseBodyAsString(), e);
            // Provide a more user-friendly error or re-throw a custom exception
            throw new RuntimeException("Failed to call Gemini API: " + e.getStatusCode() + " - " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            logger.error("Unexpected error calling Gemini API", e);
            throw new RuntimeException("An unexpected error occurred while communicating with the AI.", e);
        }
    }
}
