package com.example.goalsmanager.dto;

public class UpdateNoteRequest {

    private String content;

    // Constructors
    public UpdateNoteRequest() {
    }

    public UpdateNoteRequest(String content) {
        this.content = content;
    }

    // Getters and Setters
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
