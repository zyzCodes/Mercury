package com.example.goalsmanager.dto;

import java.time.LocalDate;

public class UpdateTaskRequest {

    private String name;
    private Boolean completed;
    private LocalDate date;

    // Constructors
    public UpdateTaskRequest() {
    }

    public UpdateTaskRequest(String name, Boolean completed, LocalDate date) {
        this.name = name;
        this.completed = completed;
        this.date = date;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}

