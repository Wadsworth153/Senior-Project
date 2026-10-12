package com.gcu.va.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gcu.va.entity.Goal;
import com.gcu.va.entity.GoalStatus;
import com.gcu.va.entity.GoalType;
import com.gcu.va.entity.Veteran;
import com.gcu.va.repository.GoalRepository;
import com.gcu.va.repository.VeteranRepository;

@Service
@Transactional
public class GoalServiceImpl implements GoalService {

    private final GoalRepository goalRepository;
    private final VeteranRepository veteranRepository;

    /**
     * Creates the service using the required repositories.
     *
     * @param goalRepository repository for goal database operations
     * @param veteranRepository repository for veteran database operations
     */
    public GoalServiceImpl(
            GoalRepository goalRepository,
            VeteranRepository veteranRepository) {
        this.goalRepository = goalRepository;
        this.veteranRepository = veteranRepository;
    }

    /**
     * Retrieves all goals belonging to a specific veteran.
     *
     * @param veteranId the ID of the veteran
     * @return the veteran's goals
     */
    @Override
    @Transactional(readOnly = true)
    public List<Goal> getGoalsByVeteran(Long veteranId) {
        return goalRepository.findByVeteran_Id(veteranId);
    }

    /**
     * Retrieves a goal only when it belongs to the specified veteran.
     *
     * @param goalId the ID of the goal
     * @param veteranId the ID of the veteran
     * @return the goal if found, otherwise null
     */
    @Override
    @Transactional(readOnly = true)
    public Goal getGoalByIdAndVeteran(Long goalId, Long veteranId) {
        return goalRepository
                .findByIdAndVeteran_Id(goalId, veteranId)
                .orElse(null);
    }

    /**
     * Creates a goal for an existing veteran.
     *
     * @param goal the goal information to save
     * @param veteranId the ID of the goal's owner
     * @return the saved goal
     */
    @Override
    public Goal createGoal(Goal goal, Long veteranId) {

        // Confirm that the veteran exists before creating the goal.
        Veteran veteran = veteranRepository.findById(veteranId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Veteran not found."));

        // Validate the title.
        if (goal.getTitle() == null || goal.getTitle().isBlank()) {
            throw new IllegalArgumentException("Goal title is required.");
        }

        if (goal.getTitle().length() > 150) {
            throw new IllegalArgumentException(
                    "Goal title cannot exceed 150 characters.");
        }

        // Validate the description length.
        if (goal.getDescription() != null
                && goal.getDescription().length() > 1000) {
            throw new IllegalArgumentException(
                    "Goal description cannot exceed 1000 characters.");
        }

        // Require a valid goal type.
        if (goal.getGoalType() == null) {
            throw new IllegalArgumentException("Goal type is required.");
        }

        // A new goal must not begin as completed.
        goal.setStatus(GoalStatus.NOT_STARTED);

        // Associate the goal with the veteran who owns it.
        goal.setVeteran(veteran);

        // Save the new goal.
        return goalRepository.save(goal);
    }

    /**
     * Updates an existing goal after checking ownership.
     *
     * @param goalId the ID of the goal to update
     * @param veteranId the ID of the owner
     * @param title the updated title
     * @param description the updated description
     * @param status the updated status
     * @return the saved goal
     */
    @Override
    public Goal updateGoal(
            Long goalId,
            Long veteranId,
            String title,
            String description,
            GoalStatus status) {

        // Find the goal only if it belongs to the current veteran.
        Goal goal = goalRepository
                .findByIdAndVeteran_Id(goalId, veteranId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Goal not found or access denied."));

        // Validate the title.
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Goal title is required.");
        }

        if (title.length() > 150) {
            throw new IllegalArgumentException(
                    "Goal title cannot exceed 150 characters.");
        }

        // Validate the description length.
        if (description != null && description.length() > 1000) {
            throw new IllegalArgumentException(
                    "Goal description cannot exceed 1000 characters.");
        }

        // Require a valid status.
        if (status == null) {
            throw new IllegalArgumentException("Goal status is required.");
        }

        // Update the permitted fields without changing ownership.
        goal.setTitle(title);
        goal.setDescription(description);
        goal.setStatus(status);

        return goalRepository.save(goal);
    }

    /**
     * Deletes a goal only if it belongs to the specified veteran.
     *
     * @param goalId the ID of the goal to delete
     * @param veteranId the ID of the owner
     */
    @Override
    public void deleteGoal(Long goalId, Long veteranId) {

        // Find the goal through its owner-specific query.
        Goal goal = goalRepository
                .findByIdAndVeteran_Id(goalId, veteranId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Goal not found or access denied."));

        // Delete the verified goal.
        goalRepository.delete(goal);
    }
}