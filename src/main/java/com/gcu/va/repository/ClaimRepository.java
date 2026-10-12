package com.gcu.va.repository;

// Import the Claim entity managed by this repository.
import com.gcu.va.entity.Claim;

// Import Spring Data JPA's repository interface for database operations.
import org.springframework.data.jpa.repository.JpaRepository;

// Import List to return multiple claim records.
import java.util.List;

/**
 * Provides database access operations for claim records.
 *
 * This repository extends JpaRepository to inherit standard database
 * operations for the Claim entity, including saving, retrieving, updating,
 * and deleting claim records.
 *
 * Spring Data JPA automatically implements the repository methods at runtime.
 */
public interface ClaimRepository extends JpaRepository<Claim, Long> {

    /**
     * Retrieves all claims associated with a specific veteran.
     *
     * Spring Data JPA derives the database query from the method name.
     * The query uses the veteran relationship's ID to find matching claims.
     *
     * @param veteranId the database identifier of the veteran
     * @return a list of claims associated with the specified veteran
     */
    List<Claim> findByVeteranId(Long veteranId);
}