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
    public UserService(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Create or update a user from any OAuth provider or JWT
     * If user exists (by provider + providerId), update their info
     * Otherwise, create a new user
     */
    @Transactional
    public UserDTO createOrUpdateUser(final CreateUserRequest request) {
        final Optional<User> existingUser = userRepository.findByProviderAndProviderId(
            request.getProvider(),
            request.getProviderId()
        );

        final User user;
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

        final User savedUser = userRepository.save(user);
        return new UserDTO(savedUser);
    }

    /**
     * Get a user by their provider and provider ID
     */
    public Optional<UserDTO> getUserByProviderAndProviderId(final String provider, final String providerId) {
        return userRepository.findByProviderAndProviderId(provider, providerId)
                .map(UserDTO::new);
    }

    /**
     * Get a user by their ID
     */
    public Optional<UserDTO> getUserById(final Long id) {
        return userRepository.findById(id)
                .map(UserDTO::new);
    }

    /**
     * Get a user by their username
     */
    public Optional<UserDTO> getUserByUsername(final String username) {
        return userRepository.findByUsername(username)
                .map(UserDTO::new);
    }

    /**
     * Get a user by their email
     */
    public Optional<UserDTO> getUserByEmail(final String email) {
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
    public boolean deleteUser(final Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Get all users by authentication provider
     */
    public List<UserDTO> getUsersByProvider(final String provider) {
        return userRepository.findByProvider(provider).stream()
                .map(UserDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Check if a user exists by provider and provider ID
     */
    public boolean existsByProviderAndProviderId(final String provider, final String providerId) {
        return userRepository.existsByProviderAndProviderId(provider, providerId);
    }

    /**
     * Helper method to update user fields from request
     */
    private void updateUserFromRequest(final User user, final CreateUserRequest request) {
        // TODO - don't update incoming data if request is sending NULL objects
        user.setUsername(request.getUsername());
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        user.setName(request.getName());
        user.setAvatarUrl(request.getAvatarUrl());
        user.setBio(request.getBio());
        user.setLocation(request.getLocation());
    }
}
