package com.example.goalsmanager.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Request DTO for creating or updating a user
 * Used when the frontend sends user data from any OAuth provider or JWT
 */
public class CreateUserRequest {
    
    @NotBlank(message = "Provider is required (github, google, jwt, etc.)")
    private String provider;
    
    @NotBlank(message = "Provider ID is required")
    private String providerId;
    
    @NotBlank(message = "Username is required")
    private String username;
    
    @Email(message = "Email should be valid")
    private String email;
    
    private String name;
    private String avatarUrl;
    private String bio;
    private String location;

    // Constructors
    public CreateUserRequest() {
    }

    public CreateUserRequest(String provider, String providerId, String username, String email) {
        this.provider = provider;
        this.providerId = providerId;
        this.username = username;
        this.email = email;
    }

    // Getters and Setters
    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
