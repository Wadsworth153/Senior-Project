package com.gcu.va.entity;

// Import JPA annotations for mapping this class to the database.
import jakarta.persistence.*;

// Import the annotation used to format date values submitted through web forms.
import org.springframework.format.annotation.DateTimeFormat;

// Import LocalDate for storing dates without time information.
import java.time.LocalDate;

// Import List to represent the claims associated with a veteran.
import java.util.List;

/**
 * Represents a veteran's information within the VA Benefits and Eligibility
 * Tracking System.
 *
 * This entity stores personal information, military service history,
 * simulated eligibility results, and relationships to the associated user
 * account and claims.
 *
 * The eligibility information maintained by this entity is part of the
 * application's educational simulation and does not represent an official
 * VA eligibility determination.
 */
@Entity
@Table(name = "veteran")
public class Veteran {

    // Primary key generated automatically by the database.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Associates the veteran record with its user account.
    // The user_id column stores the foreign key for this relationship.
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Stores the claims associated with this veteran.
    // The relationship is mapped by the "veteran" field in the Claim entity.
    // CascadeType.ALL propagates applicable persistence operations to claims.
    @OneToMany(mappedBy = "veteran", cascade = CascadeType.ALL)
    private List<Claim> claims;

    // Stores the veteran's first name.
    private String firstName;

    // Stores the veteran's last name.
    private String lastName;

    // Stores the veteran's email address.
    private String email;

    // Stores the veteran's date of birth.
    // ISO date formatting supports values submitted in yyyy-MM-dd format.
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateOfBirth;

    // Identifies the veteran's military service branch.
    private String branchOfService;

    // Records the beginning of the veteran's military service.
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate serviceStartDate;

    // Records the end of the veteran's military service.
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate serviceEndDate;

    // Stores the veteran's discharge status.
    private String dischargeStatus;

    // Stores the result of the application's simulated eligibility evaluation.
    private String eligibilityStatus;

    // Stores the explanation associated with the simulated eligibility result.
    private String eligibilityReason;

    // Getters and Setters

    /**
     * Retrieves the veteran record's database identifier.
     *
     * @return the veteran ID
     */
    public Long getId() {
        // Return the veteran ID.
        return id;
    }

    /**
     * Sets the veteran record's database identifier.
     *
     * @param id the veteran ID to assign
     */
    public void setId(Long id) {
        // Assign the supplied veteran ID.
        this.id = id;
    }

    /**
     * Retrieves the veteran's first name.
     *
     * @return the first name
     */
    public String getFirstName() {
        // Return the stored first name.
        return firstName;
    }

    /**
     * Updates the veteran's first name.
     *
     * @param firstName the first name to store
     */
    public void setFirstName(String firstName) {
        // Store the supplied first name.
        this.firstName = firstName;
    }

    /**
     * Retrieves the veteran's last name.
     *
     * @return the last name
     */
    public String getLastName() {
        // Return the stored last name.
        return lastName;
    }

    /**
     * Updates the veteran's last name.
     *
     * @param lastName the last name to store
     */
    public void setLastName(String lastName) {
        // Store the supplied last name.
        this.lastName = lastName;
    }

    /**
     * Retrieves the veteran's email address.
     *
     * @return the email address
     */
    public String getEmail() {
        // Return the stored email address.
        return email;
    }

    /**
     * Updates the veteran's email address.
     *
     * @param email the email address to store
     */
    public void setEmail(String email) {
        // Store the supplied email address.
        this.email = email;
    }

    /**
     * Retrieves the veteran's date of birth.
     *
     * @return the date of birth
     */
    public LocalDate getDateOfBirth() {
        // Return the stored date of birth.
        return dateOfBirth;
    }

    /**
     * Updates the veteran's date of birth.
     *
     * @param dateOfBirth the date of birth to store
     */
    public void setDateOfBirth(LocalDate dateOfBirth) {
        // Store the supplied date of birth.
        this.dateOfBirth = dateOfBirth;
    }

    /**
     * Retrieves the veteran's military service branch.
     *
     * @return the branch of service
     */
    public String getBranchOfService() {
        // Return the stored branch of service.
        return branchOfService;
    }

    /**
     * Updates the veteran's military service branch.
     *
     * @param branchOfService the branch of service to store
     */
    public void setBranchOfService(String branchOfService) {
        // Store the supplied branch of service.
        this.branchOfService = branchOfService;
    }

    /**
     * Retrieves the veteran's service start date.
     *
     * @return the service start date
     */
    public LocalDate getServiceStartDate() {
        // Return the stored service start date.
        return serviceStartDate;
    }

    /**
     * Updates the veteran's service start date.
     *
     * @param serviceStartDate the service start date to store
     */
    public void setServiceStartDate(LocalDate serviceStartDate) {
        // Store the supplied service start date.
        this.serviceStartDate = serviceStartDate;
    }

    /**
     * Retrieves the veteran's service end date.
     *
     * @return the service end date
     */
    public LocalDate getServiceEndDate() {
        // Return the stored service end date.
        return serviceEndDate;
    }

    /**
     * Updates the veteran's service end date.
     *
     * @param serviceEndDate the service end date to store
     */
    public void setServiceEndDate(LocalDate serviceEndDate) {
        // Store the supplied service end date.
        this.serviceEndDate = serviceEndDate;
    }

    /**
     * Retrieves the veteran's discharge status.
     *
     * @return the discharge status
     */
    public String getDischargeStatus() {
        // Return the stored discharge status.
        return dischargeStatus;
    }

    /**
     * Updates the veteran's discharge status.
     *
     * @param dischargeStatus the discharge status to store
     */
    public void setDischargeStatus(String dischargeStatus) {
        // Store the supplied discharge status.
        this.dischargeStatus = dischargeStatus;
    }

    /**
     * Associates this veteran record with a user account.
     *
     * @param user the user account associated with this veteran
     */
    public void setUser(User user) {
        // Store the associated user account.
        this.user = user;
    }

    /**
     * Retrieves the user account associated with this veteran.
     *
     * @return the associated user account
     */
    public User getUser() {
        // Return the associated user account.
        return user;
    }

    /**
     * Retrieves the simulated eligibility status.
     *
     * @return the eligibility status
     */
    public String getEligibilityStatus() {
        // Return the stored eligibility result.
        return eligibilityStatus;
    }

    /**
     * Updates the simulated eligibility status.
     *
     * @param eligibilityStatus the eligibility result to store
     */
    public void setEligibilityStatus(String eligibilityStatus) {
        // Store the supplied eligibility result.
        this.eligibilityStatus = eligibilityStatus;
    }

    /**
     * Retrieves the explanation for the simulated eligibility result.
     *
     * @return the eligibility explanation
     */
    public String getEligibilityReason() {
        // Return the stored eligibility explanation.
        return eligibilityReason;
    }

    /**
     * Updates the explanation for the simulated eligibility result.
     *
     * @param eligibilityReason the explanation to store
     */
    public void setEligibilityReason(String eligibilityReason) {
        // Store the supplied eligibility explanation.
        this.eligibilityReason = eligibilityReason;
    }

    /**
     * Retrieves the claims associated with this veteran.
     *
     * @return the list of associated claims
     */
    public List<Claim> getClaims() {
        // Return the associated claims.
        return claims;
    }

    /**
     * Updates the claims associated with this veteran.
     *
     * @param claims the list of claims to associate with this veteran
     */
    public void setClaims(List<Claim> claims) {
        // Store the supplied claims list.
        this.claims = claims;
    }
}