package com.gcu.va.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gcu.va.entity.ActionTask;

@Repository
public interface ActionTaskRepository
        extends JpaRepository<ActionTask, Long> {

    /**
     * Retrieves all tasks belonging to a specific veteran.
     *
     * @param veteranId the ID of the veteran
     * @return the veteran's tasks
     */
    List<ActionTask> findByVeteran_Id(Long veteranId);

    /**
     * Retrieves tasks belonging to a veteran filtered by task type.
     *
     * @param veteranId the ID of the veteran
     * @param taskType the type of task to retrieve
     * @return the matching tasks
     */
    List<ActionTask> findByVeteran_IdAndTaskType(
            Long veteranId,
            com.gcu.va.entity.TaskType taskType);

    /**
     * Finds a task only if it belongs to the specified veteran.
     *
     * @param id the task ID
     * @param veteranId the ID of the veteran who owns the task
     * @return the task if found and owned by the veteran
     */
    Optional<ActionTask> findByIdAndVeteran_Id(
            Long id,
            Long veteranId);
}