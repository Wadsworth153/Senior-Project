package com.gcu.va.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gcu.va.entity.ActionTask;
import com.gcu.va.entity.Goal;
import com.gcu.va.entity.TaskStatus;
import com.gcu.va.entity.TaskType;
import com.gcu.va.entity.Veteran;
import com.gcu.va.repository.ActionTaskRepository;
import com.gcu.va.repository.GoalRepository;
import com.gcu.va.repository.VeteranRepository;

/**
 * Implements the business logic for managing Action Planner tasks.
 *
 * This service handles task creation, retrieval, updating, and deletion.
 * It also verifies ownership so veterans can only manage their own tasks
 * and associate tasks with their own goals.
 */
@Service
@Transactional
public class ActionTaskServiceImpl implements ActionTaskService {

    private final ActionTaskRepository actionTaskRepository;
    private final GoalRepository goalRepository;
    private final VeteranRepository veteranRepository;

    /**
     * Constructs the service with its required repositories.
     *
     * @param actionTaskRepository repository for ActionTask entities
     * @param goalRepository repository for Goal entities
     * @param veteranRepository repository for Veteran entities
     */
    public ActionTaskServiceImpl(
            ActionTaskRepository actionTaskRepository,
            GoalRepository goalRepository,
            VeteranRepository veteranRepository) {

        this.actionTaskRepository = actionTaskRepository;
        this.goalRepository = goalRepository;
        this.veteranRepository = veteranRepository;
    }

    /**
     * Retrieves all tasks belonging to a veteran.
     *
     * @param veteranId the veteran's ID
     * @return the veteran's tasks
     */
    @Override
    @Transactional(readOnly = true)
    public List<ActionTask> getTasksByVeteran(Long veteranId) {

        return actionTaskRepository.findByVeteran_Id(veteranId);
    }

    /**
     * Retrieves tasks belonging to a veteran and matching a task type.
     *
     * @param veteranId the veteran's ID
     * @param taskType the requested task type
     * @return matching tasks
     */
    @Override
    @Transactional(readOnly = true)
    public List<ActionTask> getTasksByVeteranAndType(
            Long veteranId,
            TaskType taskType) {

        if (taskType == null) {
            throw new IllegalArgumentException(
                    "A task type must be provided.");
        }

        return actionTaskRepository
                .findByVeteran_IdAndTaskType(veteranId, taskType);
    }

    /**
     * Retrieves a task only when it belongs to the specified veteran.
     *
     * @param taskId the task's ID
     * @param veteranId the veteran's ID
     * @return the requested task
     */
    @Override
    @Transactional(readOnly = true)
    public ActionTask getTaskByIdAndVeteran(
            Long taskId,
            Long veteranId) {

        return actionTaskRepository
                .findByIdAndVeteran_Id(taskId, veteranId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Task not found or you do not have permission to access it."));
    }

    /**
     * Creates a task and associates it with the specified veteran.
     * A task may optionally be linked to one of that veteran's goals.
     *
     * New tasks always begin with NOT_STARTED status.
     *
     * @param task the task information supplied by the application
     * @param veteranId the veteran's ID
     * @param goalId the optional goal ID
     * @return the saved task
     */
    @Override
    public ActionTask createTask(
            ActionTask task,
            Long veteranId,
            Long goalId) {

        if (task == null) {
            throw new IllegalArgumentException(
                    "Task information is required.");
        }

        validateTaskDetails(
                task.getTitle(),
                task.getDescription(),
                task.getTaskType());

        Veteran veteran = veteranRepository.findById(veteranId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Veteran not found."));

        task.setVeteran(veteran);
        task.setStatus(TaskStatus.NOT_STARTED);
        task.setGoal(findVeteranGoal(goalId, veteranId));

        return actionTaskRepository.save(task);
    }

    /**
     * Updates an existing task after checking its ownership.
     *
     * @param taskId the task's ID
     * @param veteranId the veteran's ID
     * @param title the updated title
     * @param description the updated description
     * @param targetDate the optional target date
     * @param status the updated status
     * @param taskType the task type
     * @param goalId the optional associated goal ID
     * @return the updated task
     */
    @Override
    public ActionTask updateTask(
            Long taskId,
            Long veteranId,
            String title,
            String description,
            LocalDate targetDate,
            TaskStatus status,
            TaskType taskType,
            Long goalId) {

        validateTaskDetails(title, description, taskType);

        if (status == null) {
            throw new IllegalArgumentException(
                    "A task status must be selected.");
        }

        ActionTask existingTask =
                getTaskByIdAndVeteran(taskId, veteranId);

        existingTask.setTitle(title.trim());
        existingTask.setDescription(normalizeDescription(description));
        existingTask.setTargetDate(targetDate);
        existingTask.setStatus(status);
        existingTask.setTaskType(taskType);
        existingTask.setGoal(findVeteranGoal(goalId, veteranId));

        return actionTaskRepository.save(existingTask);
    }

    /**
     * Deletes a task only if it belongs to the specified veteran.
     *
     * @param taskId the task's ID
     * @param veteranId the veteran's ID
     */
    @Override
    public void deleteTask(Long taskId, Long veteranId) {

        ActionTask task = getTaskByIdAndVeteran(taskId, veteranId);

        actionTaskRepository.delete(task);
    }

    /**
     * Validates the fields shared by task creation and updating.
     *
     * @param title the task title
     * @param description the task description
     * @param taskType the task type
     */
    private void validateTaskDetails(
            String title,
            String description,
            TaskType taskType) {

        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException(
                    "Task title is required.");
        }

        if (title.trim().length() > 150) {
            throw new IllegalArgumentException(
                    "Task title cannot exceed 150 characters.");
        }

        if (description != null && description.length() > 1000) {
            throw new IllegalArgumentException(
                    "Task description cannot exceed 1000 characters.");
        }

        if (taskType == null) {
            throw new IllegalArgumentException(
                    "A task type must be selected.");
        }
    }

    /**
     * Retrieves a goal only when it belongs to the specified veteran.
     *
     * @param goalId the optional goal ID
     * @param veteranId the veteran's ID
     * @return the veteran's goal, or null when no goal was selected
     */
    private Goal findVeteranGoal(Long goalId, Long veteranId) {

        if (goalId == null) {
            return null;
        }

        return goalRepository.findByIdAndVeteran_Id(goalId, veteranId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Goal not found or you do not have permission to use it."));
    }

    /**
     * Trims a description while preserving null for an empty description.
     *
     * @param description the supplied description
     * @return the normalized description
     */
    private String normalizeDescription(String description) {

        if (description == null || description.isBlank()) {
            return null;
        }

        return description.trim();
    }
}