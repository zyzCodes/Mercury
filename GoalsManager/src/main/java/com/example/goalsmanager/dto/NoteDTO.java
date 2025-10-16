package com.example.goalsmanager.dto;

import java.time.LocalDateTime;

public class NoteDTO {

    private Long id;
    private String content;
    private Long goalId;
    private LocalDateTime createdAt;

    // Constructors
    public NoteDTO() {
    }

    public NoteDTO(Long id, String content, Long goalId, LocalDateTime createdAt) {
        this.id = id;
        this.content = content;
        this.goalId = goalId;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
