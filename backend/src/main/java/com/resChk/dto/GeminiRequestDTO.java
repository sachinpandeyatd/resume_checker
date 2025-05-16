package com.resChk.dto;

import java.util.Collections;
import java.util.List;

public class GeminiRequestDTO {
    private List<Content> contents;

    public GeminiRequestDTO(String textPrompt) {
        Part part = new Part(textPrompt);
        Content content = new Content(Collections.singletonList(part));
        this.contents = Collections.singletonList(content);
    }

    public List<Content> getContents() {
        return contents;
    }

    public void setContents(List<Content> content) {
        this.contents = content;
    }

    private static class Content {
        private List<Part> parts;

        public Content(List<Part> parts) {
            this.parts = parts;
        }

        public List<Part> getParts() {
            return parts;
        }

        public void setParts(List<Part> parts) {
            this.parts = parts;
        }
    }

    private static class Part {
        private String text;

        public Part(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}
