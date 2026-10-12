package com.gcu.va.service.impl;

// Import the registration form containing the information submitted by the user.
import com.gcu.va.dto.RegistrationForm;

// Import entity classes used to represent users, veterans, and roles.
import com.gcu.va.entity.*;

// Import repositories used to save and retrieve user and veteran records.
import com.gcu.va.repository.*;

// Import the interface implemented by this service.
import com.gcu.va.service.RegistrationService;

// Import PasswordEncoder to securely hash passwords before storing them.
import org.springframework.security.crypto.password.PasswordEncoder;

// Import Spring's Service annotation to register this class as a service bean.
import org.springframework.stereotype.Service;

// Import Transactional to manage the registration operation as a transaction.
import org.springframework.transaction.annotation.Transactional;

/**
 * Implements the registration process for veteran accounts.
 *
 * This service validates registration information, creates a user account,
 * encodes the user's password, creates the associated veteran profile,
 * and saves the profile using the appropriate repositories.
 */
@Service
public class RegistrationServiceImpl implements RegistrationService {

    // Repository responsible for retrieving and saving user accounts.
    private final UserRepository userRepository;

    // Repository responsible for saving veteran profile information.
    private final VeteranRepository veteranRepository;

    // Password encoder used to hash passwords before database storage.
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructs the registration service with its required dependencies.
     *
     * Spring injects the repositories and password encoder through this
     * constructor.
     *
     * @param userRepository repository used to access user accounts
     * @param veteranRepository repository used to access veteran profiles
     * @param passwordEncoder component used to encode user passwords
     */
    public RegistrationServiceImpl(
            UserRepository userRepository,
            VeteranRepository veteranRepository,
            PasswordEncoder passwordEncoder) {

        // Store the injected user repository.
        this.userRepository = userRepository;

        // Store the injected veteran repository.
        this.veteranRepository = veteranRepository;

        // Store the injected password encoder.
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registers a new veteran account and creates its associated profile.
     *
     * The method verifies that the passwords match and that the username
     * is not already registered. It then saves the user account, creates
     * the veteran profile, associates the profile with the user, and
     * saves the profile.
     *
     * The transaction ensures that database operations participating in
     * this transaction can be rolled back if a runtime exception occurs.
     *
     * @param form registration information submitted by the user
     * @return the saved user account
     * @throws RuntimeException if the passwords do not match or the username
     *                          already exists
     */
    @Override
    @Transactional
    public User registerVeteran(RegistrationForm form) {

        // Step 1: Verify that a password was provided and that it matches
        // the confirmation password.
        if (form.getPassword() == null
                || !form.getPassword().equals(form.getConfirmPassword())) {

            // Stop registration when the passwords do not match.
            throw new RuntimeException("Passwords do not match");
        }

        // Step 2: Check whether another account already uses this username.
        if (userRepository.findByUsername(form.getUsername()) != null) {

            // Stop registration to avoid creating a duplicate username.
            throw new RuntimeException("Username already exists");
        }

        // Step 3: Create a new user entity for the account.
        User user = new User();

        // Assign the username submitted in the registration form.
        user.setUsername(form.getUsername());

        // Encode the password before storing it in the user entity.
        user.setPassword(passwordEncoder.encode(form.getPassword()));

        // Assign the standard veteran role to the newly registered user.
        user.setRole(Role.ROLE_VETERAN);

        // Save the user account and retain the saved entity.
        user = userRepository.save(user);

        // Step 4: Create a veteran profile to store personal and service data.
        Veteran veteran = new Veteran();

        // Copy the veteran's personal information from the registration form.
        veteran.setFirstName(form.getFirstName());
        veteran.setLastName(form.getLastName());
        veteran.setEmail(form.getEmail());
        veteran.setDateOfBirth(form.getDateOfBirth());

        // Copy the veteran's military service information from the form.
        veteran.setBranchOfService(form.getBranchOfService());
        veteran.setServiceStartDate(form.getServiceStartDate());
        veteran.setServiceEndDate(form.getServiceEndDate());
        veteran.setDischargeStatus(form.getDischargeStatus());

        // Step 5: Associate the veteran profile with the saved user account.
        veteran.setUser(user);

        // Step 6: Save the veteran profile in the database.
        veteranRepository.save(veteran);

        // Step 7: Return the saved user account to the calling component.
        return user;
    }
}