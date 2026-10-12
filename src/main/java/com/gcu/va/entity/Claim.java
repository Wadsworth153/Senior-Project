package com.gcu.va.entity;

// Import JPA annotations for mapping this class to a database table.
import jakarta.persistence.*;

// Import LocalDate to store the claim submission date without a time component.
import java.time.LocalDate;

/**
 * Represents a claim submitted by a veteran.
 *
 * This entity maps to the claims database table and stores the claim's
 * identifier, type, status, submission date, description, and associated veteran.
 */
@Entity
@Table(name = "claims")
public class Claim {

    // Primary key generated automatically by the database.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Identifies the type of claim, such as Disability, Education, or Healthcare.
    private String claimType;

    // Stores the claim status as its enum name in the database.
    @Enumerated(EnumType.STRING)
    private ClaimStatus status;

    // Records the date the claim was submitted.
    private LocalDate submittedDate;

    // Stores additional information describing the claim.
    private String description;

    // Associates this claim with the veteran who submitted it.
    // Multiple claims can be associated with the same veteran.
    @ManyToOne
    @JoinColumn(name = "veteran_id")
    private Veteran veteran;

    // Getters and Setters

    /**
     * Retrieves the claim's database identifier.
     *
     * @return the claim ID
     */
    public Long getId() {
        // Return the claim ID.
        return id;
    }

    /**
     * Sets the claim's database identifier.
     *
     * @param id the claim ID to assign
     */
    public void setId(Long id) {
        // Assign the supplied claim ID.
        this.id = id;
    }

    /**
     * Retrieves the claim type.
     *
     * @return the claim type
     */
    public String getClaimType() {
        // Return the claim type.
        return claimType;
    }

    /**
     * Sets the claim type.
     *
     * @param claimType the type of claim to assign
     */
    public void setClaimType(String claimType) {
        // Assign the supplied claim type.
        this.claimType = claimType;
    }

    /**
     * Retrieves the current claim status.
     *
     * @return the claim status
     */
    public ClaimStatus getStatus() {
        // Return the current claim status.
        return status;
    }

    /**
     * Sets the claim status.
     *
     * @param status the claim status to assign
     */
    public void setStatus(ClaimStatus status) {
        // Assign the supplied claim status.
        this.status = status;
    }

    /**
     * Retrieves the claim submission date.
     *
     * @return the submission date
     */
    public LocalDate getSubmittedDate() {
        // Return the submission date.
        return submittedDate;
    }

    /**
     * Sets the claim submission date.
     *
     * @param submittedDate the submission date to assign
     */
    public void setSubmittedDate(LocalDate submittedDate) {
        // Assign the supplied submission date.
        this.submittedDate = submittedDate;
    }

    /**
     * Retrieves the claim description.
     *
     * @return the claim description
     */
    public String getDescription() {
        // Return the claim description.
        return description;
    }

    /**
     * Sets the claim description.
     *
     * @param description the description to assign to the claim
     */
    public void setDescription(String description) {
        // Assign the supplied claim description.
        this.description = description;
    }

    /**
     * Retrieves the veteran associated with this claim.
     *
     * @return the associated veteran
     */
    public Veteran getVeteran() {
        // Return the associated veteran.
        return veteran;
    }

    /**
     * Associates this claim with a veteran.
     *
     * @param veteran the veteran associated with the claim
     */
    public void setVeteran(Veteran veteran) {
        // Assign the associated veteran.
        this.veteran = veteran;
    }
}