package com.gcu.va.service;

import java.time.LocalDate;
import java.util.List;

import com.gcu.va.entity.ActionTask;
import com.gcu.va.entity.TaskStatus;
import com.gcu.va.entity.TaskType;

/**
 * Defines the operations available for managing Action Planner tasks.
 *
 * Tasks can be associated with a veteran and optionally linked
 * to one of that veteran's benefits goals.
 */
public interface ActionTaskService {

    /**
     * Retrieves all tasks belonging to a specific veteran.
     *
     * @param veteranId the ID of the veteran
     * @return a list of the veteran's tasks
     */
    List<ActionTask> getTasksByVeteran(Long veteranId);

    /**
     * Retrieves a veteran's tasks filtered by task type.
     *
     * @param veteranId the ID of the veteran
     * @param taskType the type of tasks to retrieve
     * @return a list of matching tasks
     */
    List<ActionTask> getTasksByVeteranAndType(
            Long veteranId,
            TaskType taskType);

    /**
     * Retrieves a specific task only if it belongs to the veteran.
     *
     * @param taskId the ID of the task
     * @param veteranId the ID of the veteran
     * @return the requested task
     */
    ActionTask getTaskByIdAndVeteran(
            Long taskId,
            Long veteranId);

    /**
     * Creates a task for a veteran and optionally associates it
     * with one of the veteran's existing goals.
     *
     * @param task the task information
     * @param veteranId the ID of the veteran
     * @param goalId the optional associated goal ID
     * @return the saved task
     */
    ActionTask createTask(
            ActionTask task,
            Long veteranId,
            Long goalId);

    /**
     * Updates a task after verifying that it belongs to the veteran.
     *
     * @param taskId the ID of the task
     * @param veteranId the ID of the veteran
     * @param title the updated task title
     * @param description the updated task description
     * @param targetDate the optional target date
     * @param status the updated task status
     * @param taskType the task type
     * @param goalId the optional associated goal ID
     * @return the updated task
     */
    ActionTask updateTask(
            Long taskId,
            Long veteranId,
            String title,
            String description,
            LocalDate targetDate,
            TaskStatus status,
            TaskType taskType,
            Long goalId);

    /**
     * Deletes a task only if it belongs to the specified veteran.
     *
     * @param taskId the ID of the task
     * @param veteranId the ID of the veteran
     */
    void deleteTask(Long taskId, Long veteranId);
}