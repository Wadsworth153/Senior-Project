package com.gcu.va.service;

import java.util.List;

import com.gcu.va.entity.Goal;
import com.gcu.va.entity.GoalStatus;

public interface GoalService {

    /**
     * Retrieves all goals belonging to a specific veteran.
     *
     * @param veteranId the ID of the veteran
     * @return the veteran's goals
     */
    List<Goal> getGoalsByVeteran(Long veteranId);

    /**
     * Retrieves a specific goal belonging to a specific veteran.
     *
     * @param goalId the ID of the goal
     * @param veteranId the ID of the veteran
     * @return the goal, or null if it does not belong to the veteran
     */
    Goal getGoalByIdAndVeteran(Long goalId, Long veteranId);

    /**
     * Creates a predefined or custom goal for a veteran.
     *
     * @param goal the goal information
     * @param veteranId the ID of the veteran who owns the goal
     * @return the saved goal
     */
    Goal createGoal(Goal goal, Long veteranId);

    /**
     * Updates a goal after verifying ownership.
     *
     * @param goalId the ID of the goal
     * @param veteranId the ID of the veteran who owns the goal
     * @param title the updated title
     * @param description the updated description
     * @param status the updated status
     * @return the updated goal
     */
    Goal updateGoal(
            Long goalId,
            Long veteranId,
            String title,
            String description,
            GoalStatus status);

    /**
     * Deletes a goal after verifying ownership.
     *
     * @param goalId the ID of the goal
     * @param veteranId the ID of the veteran who owns the goal
     */
    void deleteGoal(Long goalId, Long veteranId);
}