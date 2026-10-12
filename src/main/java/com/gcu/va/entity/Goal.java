package com.gcu.va.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Represents a personal goal within the VA Benefits Eligibility
 * and Tracking System.
 *
 * A goal can be selected from the predefined benefit categories
 * or created as a custom goal by a veteran.
 *
 * Each goal belongs to a specific veteran and includes a title,
 * description, type, status, and creation date.
 *
 * Goals are intended to help veterans organize their benefit
 * research and personal planning. They do not represent official
 * VA eligibility determinations or application requirements.
 */
@Entity
@Table(name = "goals")
public class Goal {

    // Primary key generated automatically by the database.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Stores the name of the goal.
    @Column(nullable = false, length = 150)
    private String title;

    // Stores an optional description of the goal.
    @Column(length = 1000)
    private String description;

    // Stores whether the goal is predefined or custom.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GoalType goalType;

    // Tracks the veteran's progress toward completing the goal.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GoalStatus status;

    // Records when the goal was created.
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Associates the goal with the veteran who owns it.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "veteran_id", nullable = false)
    private Veteran veteran;

    /**
     * Assigns the creation date and default status before
     * the goal is first saved to the database.
     */
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }

        if (status == null) {
            status = GoalStatus.NOT_STARTED;
        }
    }

    /**
     * Retrieves the goal's database identifier.
     *
     * @return the goal ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the goal's database identifier.
     *
     * @param id the goal ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Retrieves the goal title.
     *
     * @return the goal title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the goal title.
     *
     * @param title the title to assign
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Retrieves the goal description.
     *
     * @return the goal description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the goal description.
     *
     * @param description the description to assign
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Retrieves the goal type.
     *
     * @return the goal type
     */
    public GoalType getGoalType() {
        return goalType;
    }

    /**
     * Sets the goal type.
     *
     * @param goalType the goal type to assign
     */
    public void setGoalType(GoalType goalType) {
        this.goalType = goalType;
    }

    /**
     * Retrieves the current goal status.
     *
     * @return the goal status
     */
    public GoalStatus getStatus() {
        return status;
    }

    /**
     * Sets the goal status.
     *
     * @param status the status to assign
     */
    public void setStatus(GoalStatus status) {
        this.status = status;
    }

    /**
     * Retrieves the goal creation date and time.
     *
     * @return the creation timestamp
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the goal creation date and time.
     *
     * @param createdAt the creation timestamp
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Retrieves the veteran who owns this goal.
     *
     * @return the associated veteran
     */
    public Veteran getVeteran() {
        return veteran;
    }

    /**
     * Associates this goal with its owner.
     *
     * @param veteran the veteran who owns the goal
     */
    public void setVeteran(Veteran veteran) {
        this.veteran = veteran;
    }
}