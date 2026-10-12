package com.gcu.va.dto;

// Import LocalDate for storing dates without time information.
import java.time.LocalDate;

// Import the annotation used to format dates submitted through web forms.
import org.springframework.format.annotation.DateTimeFormat;

// Import validation annotations for email addresses, required values, and field lengths.
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object (DTO) for collecting and validating registration information.
 *
 * This class holds the login credentials and veteran information submitted
 * through the registration form. Validation annotations define requirements
 * that submitted values must satisfy before registration can proceed.
 */
public class RegistrationForm {

    // Login Information

    // Require a username containing between 4 and 50 characters.
    @NotBlank(message = "Username is required.")
    @Size(min = 4, max = 50, message = "Username must be between 4 and 50 characters.")
    private String username;

    // Require a password containing at least 8 characters.
    @NotBlank(message = "Password is required.")
    @Size(min = 8, message = "Password must be at least 8 characters.")
    private String password;

    // Require the user to enter a confirmation password.
    // Matching the password and confirmation is handled by application logic.
    @NotBlank(message = "Please confirm your password.")
    private String confirmPassword;

    // Veteran Information

    // Require the veteran's first name.
    @NotBlank(message = "First name is required.")
    private String firstName;

    // Require the veteran's last name.
    @NotBlank(message = "Last name is required.")
    private String lastName;

    // Require an email address and validate its general email format.
    @NotBlank(message = "Email address is required.")
    @Email(message = "Please enter a valid email address.")
    private String email;

    // Require a date of birth that is earlier than the current date.
    // ISO date formatting supports values submitted in yyyy-MM-dd format.
    @NotNull(message = "Date of birth is required.")
    @Past(message = "Date of birth must be in the past.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateOfBirth;

    // Require the veteran to identify their branch of service.
    @NotBlank(message = "Branch of service is required.")
    private String branchOfService;

    // Require the veteran's service start date.
    // ISO date formatting allows Spring to convert submitted date values.
    @NotNull(message = "Service start date is required.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate serviceStartDate;

    // Require the veteran's service end date.
    // Additional application logic can validate the relationship between dates.
    @NotNull(message = "Service end date is required.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate serviceEndDate;

    // Require the veteran to identify their discharge status.
    @NotBlank(message = "Discharge status is required.")
    private String dischargeStatus;

    // Getters and Setters

    /**
     * Retrieves the submitted username.
     *
     * @return the username
     */
    public String getUsername() {
        // Return the stored username.
        return username;
    }

    /**
     * Updates the username.
     *
     * @param username the username to store
     */
    public void setUsername(String username) {
        // Store the supplied username.
        this.username = username;
    }

    /**
     * Retrieves the submitted password.
     *
     * @return the password
     */
    public String getPassword() {
        // Return the stored password.
        return password;
    }

    /**
     * Updates the password.
     *
     * @param password the password to store
     */
    public void setPassword(String password) {
        // Store the supplied password.
        this.password = password;
    }

    /**
     * Retrieves the password confirmation.
     *
     * @return the confirmation password
     */
    public String getConfirmPassword() {
        // Return the stored confirmation password.
        return confirmPassword;
    }

    /**
     * Updates the password confirmation.
     *
     * @param confirmPassword the confirmation password to store
     */
    public void setConfirmPassword(String confirmPassword) {
        // Store the supplied confirmation password.
        this.confirmPassword = confirmPassword;
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
     * Retrieves the veteran's branch of service.
     *
     * @return the branch of service
     */
    public String getBranchOfService() {
        // Return the stored branch of service.
        return branchOfService;
    }

    /**
     * Updates the veteran's branch of service.
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
}