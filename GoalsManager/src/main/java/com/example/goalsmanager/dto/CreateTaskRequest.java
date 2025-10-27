package com.example.goalsmanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class CreateTaskRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Date is required")
    private LocalDate date;

    @NotNull(message = "Habit ID is required")
    private Long habitId;

    @NotNull(message = "User ID is required")
    private Long userId;

    // Constructors
    public CreateTaskRequest() {
    }

    public CreateTaskRequest(final String name, final LocalDate date, final Long habitId, final Long userId) {
        this.name = name;
        this.date = date;
        this.habitId = habitId;
        this.userId = userId;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(final LocalDate date) {
        this.date = date;
    }

    public Long getHabitId() {
        return habitId;
    }

    public void setHabitId(final Long habitId) {
        this.habitId = habitId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(final Long userId) {
        this.userId = userId;
    }
}

