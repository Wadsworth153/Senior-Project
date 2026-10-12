package com.gcu.va.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gcu.va.entity.Goal;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {

    /**
     * Retrieves all goals belonging to a specific veteran.
     *
     * @param veteranId the ID of the veteran
     * @return a list of goals owned by that veteran
     */
    List<Goal> findByVeteran_Id(Long veteranId);

    /**
     * Retrieves a specific goal belonging to a specific veteran.
     *
     * This method helps prevent a veteran from accessing another
     * veteran's goal through an ID-based request.
     *
     * @param id the goal ID
     * @param veteranId the ID of the veteran who owns the goal
     * @return an Optional containing the goal if it belongs to the veteran
     */
    Optional<Goal> findByIdAndVeteran_Id(Long id, Long veteranId);
}