package com.yuvaansh.fitness_tracker_api.repository;

import com.yuvaansh.fitness_tracker_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * UserRepository - Interface for database operations on User entity
 * 
 * 
 * JpaRepository<User, Long> means:
 * - User = the entity type (what table to work with)
 * - Long = the type of the ID field (User.id is a Long)
 * 
 **/

@Repository 
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Custom method to find a user by username
     * 
     * Spring Data JPA automatically implements this method based on the name!
     */
    
    Optional<User> findByUsername(String username);
    
    /**
     * Check if a username already exists
     * 
     * Spring automatically creates: SELECT COUNT(*) > 0 FROM users WHERE username = ?
     */
    boolean existsByUsername(String username);
}
