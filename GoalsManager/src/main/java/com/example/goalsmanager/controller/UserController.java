package com.example.goalsmanager.controller;

import com.example.goalsmanager.dto.CreateUserRequest;
import com.example.goalsmanager.dto.UserDTO;
import com.example.goalsmanager.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(final UserService userService) {
        this.userService = userService;
    }

    /**
     * Create or update a user from GitHub OAuth
     * POST /api/users
     */
    @PostMapping
    public ResponseEntity<UserDTO> createOrUpdateUser(@Valid @RequestBody final CreateUserRequest request) {
        final UserDTO user = userService.createOrUpdateUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    /**
     * Get all users
     * GET /api/users
     */
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        final List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Get a user by ID
     * GET /api/users/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable final Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get a user by provider and provider ID
     * GET /api/users/provider/{provider}/{providerId}
     */
    @GetMapping("/provider/{provider}/{providerId}")
    public ResponseEntity<UserDTO> getUserByProviderAndProviderId(
            @PathVariable final String provider,
            @PathVariable final String providerId) {
        return userService.getUserByProviderAndProviderId(provider, providerId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get all users by provider
     * GET /api/users/provider/{provider}
     */
    @GetMapping("/provider/{provider}")
    public ResponseEntity<List<UserDTO>> getUsersByProvider(@PathVariable final String provider) {
        final List<UserDTO> users = userService.getUsersByProvider(provider);
        return ResponseEntity.ok(users);
    }

    /**
     * Get a user by username
     * GET /api/users/username/{username}
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable final String username) {
        return userService.getUserByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get a user by email
     * GET /api/users/email/{email}
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<UserDTO> getUserByEmail(@PathVariable final String email) {
        return userService.getUserByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Delete a user by ID
     * DELETE /api/users/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable final Long id) {
        final boolean deleted = userService.deleteUser(id);
        if (deleted) {
            return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User not found"));
        }
    }

    /**
     * Check if a user exists by provider and provider ID
     * GET /api/users/exists/provider/{provider}/{providerId}
     */
    @GetMapping("/exists/provider/{provider}/{providerId}")
    public ResponseEntity<Map<String, Boolean>> checkUserExistsByProvider(
            @PathVariable final String provider,
            @PathVariable final String providerId) {
        final boolean exists = userService.existsByProviderAndProviderId(provider, providerId);
        return ResponseEntity.ok(Map.of("exists", exists));
    }
}
