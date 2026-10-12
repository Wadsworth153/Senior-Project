package com.gcu.va.service;

// Import the DTO containing the information submitted during registration.
import com.gcu.va.dto.RegistrationForm;

// Import the User entity representing an application account.
import com.gcu.va.entity.User;

/**
 * Defines the service operations available for registering veteran accounts.
 *
 * Implementing classes provide the logic for validating registration
 * information and creating the user account and associated veteran profile.
 */
public interface RegistrationService {

    /**
     * Registers a veteran using the information supplied in the registration form.
     *
     * The implementation handles the registration workflow, including
     * validating password confirmation, checking username availability,
     * creating the user account, and saving the associated veteran profile.
     *
     * @param form registration information submitted by the user
     * @return the registered user account
     */
    User registerVeteran(RegistrationForm form);
}