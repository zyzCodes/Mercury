package com.example.goalsmanager.service;

import com.example.goalsmanager.dto.CreateUserRequest;
import com.example.goalsmanager.dto.UserDTO;
import com.example.goalsmanager.model.User;
import com.example.goalsmanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Create or update a user from any OAuth provider or JWT
     * If user exists (by provider + providerId), update their info
     * Otherwise, create a new user
     */
    @Transactional
    public UserDTO createOrUpdateUser(CreateUserRequest request) {
        Optional<User> existingUser = userRepository.findByProviderAndProviderId(
            request.getProvider(), 
            request.getProviderId()
        );
        
        User user;
        if (existingUser.isPresent()) {
            // Update existing user
            user = existingUser.get();
            updateUserFromRequest(user, request);
        } else {
            // Create new user
            user = new User();
            user.setProvider(request.getProvider());
            user.setProviderId(request.getProviderId());
            updateUserFromRequest(user, request);
        }
        
        User savedUser = userRepository.save(user);
        return new UserDTO(savedUser);
    }

    /**
     * Get a user by their provider and provider ID
     */
    public Optional<UserDTO> getUserByProviderAndProviderId(String provider, String providerId) {
        return userRepository.findByProviderAndProviderId(provider, providerId)
                .map(UserDTO::new);
    }

    /**
     * Get a user by their ID
     */
    public Optional<UserDTO> getUserById(Long id) {
        return userRepository.findById(id)
                .map(UserDTO::new);
    }

    /**
     * Get a user by their GitHub ID
     */
    public Optional<UserDTO> getUserByGithubId(String githubId) {
        return userRepository.findByGithubId(githubId)
                .map(UserDTO::new);
    }

    /**
     * Get a user by their username
     */
    public Optional<UserDTO> getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(UserDTO::new);
    }

    /**
     * Get a user by their email
     */
    public Optional<UserDTO> getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(UserDTO::new);
    }

    /**
     * Get all users
     */
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Delete a user by their ID
     */
    @Transactional
    public boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Get all users by authentication provider
     */
    public List<UserDTO> getUsersByProvider(String provider) {
        return userRepository.findByProvider(provider).stream()
                .map(UserDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Check if a user exists by provider and provider ID
     */
    public boolean existsByProviderAndProviderId(String provider, String providerId) {
        return userRepository.existsByProviderAndProviderId(provider, providerId);
    }

    /**
     * Check if a user exists by GitHub ID (legacy support)
     */
    public boolean existsByGithubId(String githubId) {
        return userRepository.existsByGithubId(githubId);
    }

    /**
     * Helper method to update user fields from request
     */
    private void updateUserFromRequest(User user, CreateUserRequest request) {
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setName(request.getName());
        user.setAvatarUrl(request.getAvatarUrl());
        user.setBio(request.getBio());
        user.setLocation(request.getLocation());
        // Set githubId if provided (for backward compatibility)
        if (request.getGithubId() != null) {
            user.setGithubId(request.getGithubId());
        }
    }
}
