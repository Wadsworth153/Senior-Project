package com.gcu.va.repository;

// Import List to support returning multiple user records.
import java.util.List;

// Import Spring Data JPA's repository interface for database operations.
import org.springframework.data.jpa.repository.JpaRepository;

// Import the User entity managed by this repository.
import com.gcu.va.entity.User;

/**
 * Provides database access operations for user accounts.
 *
 * This repository extends JpaRepository to inherit standard operations
 * for saving, retrieving, updating, and deleting User entities.
 *
 * Spring Data JPA automatically implements the repository methods at runtime.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user account by its exact username.
     *
     * Spring Data JPA derives the database query from the method name.
     *
     * @param username the username to search for
     * @return the matching user, or null if no matching user is found
     */
    User findByUsername(String username);

    /**
     * Finds user accounts whose usernames contain the supplied text.
     *
     * Spring Data JPA derives a query that searches for usernames
     * containing the provided value.
     *
     * @param username the text to search for within usernames
     * @return a list of users whose usernames contain the supplied text
     */
    List<User> findByUsernameContaining(String username);
}