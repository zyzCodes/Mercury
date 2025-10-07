package com.example.goalsmanager.repository;

import com.example.goalsmanager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Find a user by their provider and provider ID
     * This is the primary method for finding users by their authentication provider
     * @param provider The authentication provider (github, google, jwt, etc.)
     * @param providerId The provider-specific user ID
     * @return Optional containing the user if found
     */
    Optional<User> findByProviderAndProviderId(String provider, String providerId);
    
    /**
     * Find a user by their GitHub ID (legacy support)
     * @param githubId The GitHub ID of the user
     * @return Optional containing the user if found
     */
    Optional<User> findByGithubId(String githubId);
    
    /**
     * Find a user by their username
     * @param username The username of the user
     * @return Optional containing the user if found
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Find a user by their email
     * @param email The email of the user
     * @return Optional containing the user if found
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Find all users by authentication provider
     * @param provider The authentication provider
     * @return List of users using that provider
     */
    List<User> findByProvider(String provider);
    
    /**
     * Check if a user exists with the given provider and provider ID
     * @param provider The authentication provider
     * @param providerId The provider-specific user ID
     * @return true if user exists, false otherwise
     */
    boolean existsByProviderAndProviderId(String provider, String providerId);
    
    /**
     * Check if a user exists with the given GitHub ID (legacy support)
     * @param githubId The GitHub ID to check
     * @return true if user exists, false otherwise
     */
    boolean existsByGithubId(String githubId);
    
    /**
     * Check if a user exists with the given username
     * @param username The username to check
     * @return true if user exists, false otherwise
     */
    boolean existsByUsername(String username);
    
    /**
     * Check if a user exists with the given email
     * @param email The email to check
     * @return true if user exists, false otherwise
     */
    boolean existsByEmail(String email);
}
