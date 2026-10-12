package com.gcu.va.repository;

// Import the Veteran entity managed by this repository.
import com.gcu.va.entity.Veteran;

// Import Spring Data JPA's repository interface for database operations.
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Provides database access operations for veteran records.
 *
 * This repository extends JpaRepository to inherit standard database
 * operations for saving, retrieving, updating, and deleting Veteran entities.
 *
 * Spring Data JPA automatically implements repository methods at runtime.
 */
public interface VeteranRepository extends JpaRepository<Veteran, Long> {

    /**
     * Finds a veteran record associated with a specific user account.
     *
     * Spring Data JPA derives the database query from the method name.
     * The query uses the ID of the user associated with the veteran record.
     *
     * @param userId the database identifier of the associated user account
     * @return the matching veteran record, or null if no match is found
     */
    Veteran findByUserId(Long userId);
}