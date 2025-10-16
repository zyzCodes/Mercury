package com.example.goalsmanager.dto;

import java.time.LocalDate;

public class UpdateHabitRequest {

    private String name;
    private String description;
    private String daysOfWeek;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer streakStatus;
    private String color;

    // Constructors
    public UpdateHabitRequest() {
    }

    public UpdateHabitRequest(String name, String description, String daysOfWeek,
                              LocalDate startDate, LocalDate endDate, Integer streakStatus, String color) {
        this.name = name;
        this.description = description;
        this.daysOfWeek = daysOfWeek;
        this.startDate = startDate;
        this.endDate = endDate;
        this.streakStatus = streakStatus;
        this.color = color;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDaysOfWeek() {
        return daysOfWeek;
    }

    public void setDaysOfWeek(String daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Integer getStreakStatus() {
        return streakStatus;
    }

    public void setStreakStatus(Integer streakStatus) {
        this.streakStatus = streakStatus;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}

