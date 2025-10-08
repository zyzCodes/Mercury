package com.example.goalsmanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateNoteRequest {

    @NotBlank(message = "Content is required")
    private String content;

    @NotNull(message = "Goal ID is required")
    private Long goalId;

    // Constructors
    public CreateNoteRequest() {
    }

    public CreateNoteRequest(String content, Long goalId) {
        this.content = content;
        this.goalId = goalId;
    }

    // Getters and Setters
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getGoalId() {
        return goalId;
    }

    public void setGoalId(Long goalId) {
        this.goalId = goalId;
    }
}
