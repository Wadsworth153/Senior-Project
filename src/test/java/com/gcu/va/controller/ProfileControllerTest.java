package com.gcu.va.controller;

// Import JUnit assertions for checking expected values and exceptions.
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

// Import Mockito methods for verifying interactions and configuring mock behavior.
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Import LocalDate for creating and verifying service dates.
import java.time.LocalDate;

// Import entities used to represent the test user and veteran.
import com.gcu.va.entity.Role;
import com.gcu.va.entity.User;
import com.gcu.va.entity.Veteran;

// Import repositories used to retrieve and save user and veteran records.
import com.gcu.va.repository.UserRepository;
import com.gcu.va.repository.VeteranRepository;

// Import the service responsible for calculating eligibility information.
import com.gcu.va.service.EligibilityService;

// Import JUnit annotations for test methods, setup, and Mockito integration.
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

// Import Mockito annotations for creating and injecting mock dependencies.
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

// Import the exception expected when access is denied.
import org.springframework.security.access.AccessDeniedException;

// Import Authentication to simulate the currently logged-in user.
import org.springframework.security.core.Authentication;

// Import Spring MVC model types for checking controller attributes.
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

/**
 * Tests the profile-management functionality implemented by ProfileController.
 *
 * These tests verify profile display, eligibility information, editing of
 * military service information, validation of submitted dates, and rejection
 * of requests involving unknown users or missing veteran profiles.
 *
 * Mockito provides simulated dependencies so the controller can be tested
 * without performing actual database operations.
 */
@ExtendWith(MockitoExtension.class)
class ProfileControllerTest {

    // Mock the repository used to retrieve user accounts.
    @Mock
    private UserRepository userRepository;

    // Mock the repository used to retrieve and save veteran records.
    @Mock
    private VeteranRepository veteranRepository;

    // Mock the service responsible for calculating eligibility information.
    @Mock
    private EligibilityService eligibilityService;

    // Mock authentication information for the current user.
    @Mock
    private Authentication authentication;

    // Create the controller and inject its mocked dependencies.
    @InjectMocks
    private ProfileController profileController;

    // Store the test user account.
    private User user;

    // Store the veteran profile associated with the test user.
    private Veteran veteran;

    // Store attributes populated by the controller.
    private Model model;

    /**
     * Initializes shared test data before each test executes.
     *
     * Creates a veteran user and associated profile, initializes the model,
     * and configures authentication to return the test username.
     */
    @BeforeEach
    void setUp() {

        // Create a test user account.
        user = new User();

        // Assign an identifier to the test user.
        user.setId(1L);

        // Assign the test username.
        user.setUsername("testveteran");

        // Assign the veteran role.
        user.setRole(Role.ROLE_VETERAN);

        // Create a veteran profile for the test user.
        veteran = new Veteran();

        // Assign an identifier to the veteran profile.
        veteran.setId(10L);

        // Set the veteran's first name.
        veteran.setFirstName("Test");

        // Set the veteran's last name.
        veteran.setLastName("Veteran");

        // Associate the veteran profile with the user account.
        veteran.setUser(user);

        // Associate the veteran profile with the user in the opposite direction.
        user.setVeteran(veteran);

        // Initialize a model for capturing controller attributes.
        model = new ExtendedModelMap();

        // Configure authentication to identify the test user.
        when(authentication.getName()).thenReturn("testveteran");
    }

    /**
     * Verifies that the profile page displays veteran and eligibility information.
     *
     * Checks the returned view, model attributes, and calls made to the
     * veteran repository and eligibility service.
     */
    @Test
    void profileDisplaysVeteranAndEligibilityInformation() {

        // Return the test user when the repository is queried.
        when(userRepository.findByUsername("testveteran"))
                .thenReturn(user);

        // Return the veteran profile associated with the user ID.
        when(veteranRepository.findByUserId(1L))
                .thenReturn(veteran);

        // Configure the eligibility service to return an eligible status.
        when(eligibilityService.calculateStatus(veteran))
                .thenReturn("Eligible");

        // Configure the eligibility service to return an explanation.
        when(eligibilityService.calculateReason(veteran))
                .thenReturn("Meets the prototype requirements.");

        // Invoke the controller method that displays the profile.
        String result = profileController.myProfile(authentication, model);

        // Verify that the controller returns the profile template.
        assertEquals("my-profile", result);

        // Verify that the model contains the user account.
        assertEquals(user, model.getAttribute("user"));

        // Verify that the model contains the veteran profile.
        assertEquals(veteran, model.getAttribute("veteran"));

        // Verify that the eligibility status is included in the model.
        assertEquals("Eligible", model.getAttribute("eligibilityStatus"));

        // Verify that the eligibility explanation is included in the model.
        assertEquals(
                "Meets the prototype requirements.",
                model.getAttribute("eligibilityReason")
        );

        // Verify that the repository retrieves the correct veteran profile.
        verify(veteranRepository).findByUserId(1L);

        // Verify that the eligibility status is calculated for the veteran.
        verify(eligibilityService).calculateStatus(veteran);

        // Verify that the eligibility explanation is calculated for the veteran.
        verify(eligibilityService).calculateReason(veteran);
    }

    /**
     * Verifies that the profile page handles a missing veteran profile.
     *
     * Checks that the profile view still loads and that the model contains
     * the eligibility information returned for a missing profile.
     */
    @Test
    void profileHandlesMissingVeteranProfile() {

        // Return the test user when the repository is queried.
        when(userRepository.findByUsername("testveteran"))
                .thenReturn(user);

        // Simulate a user account without a corresponding veteran record.
        when(veteranRepository.findByUserId(1L))
                .thenReturn(null);

        // Configure the eligibility service to return an ineligible status.
        when(eligibilityService.calculateStatus(null))
                .thenReturn("Ineligible");

        // Configure the service to explain that the profile is missing.
        when(eligibilityService.calculateReason(null))
                .thenReturn("No veteran profile is available.");

        // Invoke the controller method that displays the profile.
        String result = profileController.myProfile(authentication, model);

        // Verify that the controller returns the profile template.
        assertEquals("my-profile", result);

        // Verify that no veteran profile is included in the model.
        assertNull(model.getAttribute("veteran"));

        // Verify that the returned eligibility status is included.
        assertEquals(
                "Ineligible",
                model.getAttribute("eligibilityStatus")
        );

        // Verify that the missing-profile explanation is included.
        assertEquals(
                "No veteran profile is available.",
                model.getAttribute("eligibilityReason")
        );
    }

    /**
     * Verifies that the profile page rejects an unknown user.
     *
     * The controller is expected to throw AccessDeniedException when the
     * authenticated username cannot be found in the user repository.
     */
    @Test
    void profileRejectsUnknownUser() {

        // Simulate a repository lookup that finds no matching user.
        when(userRepository.findByUsername("testveteran"))
                .thenReturn(null);

        // Verify that attempting to display the profile throws an exception.
        assertThrows(
                AccessDeniedException.class,
                () -> profileController.myProfile(authentication, model)
        );
    }

    /**
     * Verifies that the service-information edit form displays existing data.
     *
     * Checks that the controller returns the edit template, adds the veteran
     * profile to the model, and retrieves the correct veteran record.
     */
    @Test
    void editFormDisplaysExistingVeteranInformation() {

        // Return the test user when the repository is queried.
        when(userRepository.findByUsername("testveteran"))
                .thenReturn(user);

        // Return the veteran profile associated with the user ID.
        when(veteranRepository.findByUserId(1L))
                .thenReturn(veteran);

        // Invoke the controller method that displays the edit form.
        String result =
                profileController.showEditForm(authentication, model);

        // Verify that the controller returns the service-information edit template.
        assertEquals("edit-service-info", result);

        // Verify that the model contains the existing veteran information.
        assertEquals(veteran, model.getAttribute("veteran"));

        // Verify that the repository retrieves the correct veteran record.
        verify(veteranRepository).findByUserId(1L);
    }

    /**
     * Verifies that the edit form rejects an unknown user.
     *
     * The controller is expected to throw AccessDeniedException when
     * the authenticated username does not exist.
     */
    @Test
    void editFormRejectsUnknownUser() {

        // Simulate a repository lookup that finds no matching user.
        when(userRepository.findByUsername("testveteran"))
                .thenReturn(null);

        // Verify that attempting to open the edit form throws an exception.
        assertThrows(
                AccessDeniedException.class,
                () -> profileController.showEditForm(authentication, model)
        );
    }

    /**
     * Verifies that the edit form rejects a user without a veteran profile.
     *
     * The controller is expected to throw AccessDeniedException when
     * the user exists but the corresponding veteran record is missing.
     */
    @Test
    void editFormRejectsMissingVeteranProfile() {

        // Return the test user when the repository is queried.
        when(userRepository.findByUsername("testveteran"))
                .thenReturn(user);

        // Simulate a missing veteran record.
        when(veteranRepository.findByUserId(1L))
                .thenReturn(null);

        // Verify that attempting to open the edit form throws an exception.
        assertThrows(
                AccessDeniedException.class,
                () -> profileController.showEditForm(authentication, model)
        );
    }

    /**
     * Verifies that valid military service information can be updated.
     *
     * Checks that the controller redirects to the profile page, updates
     * the veteran's service fields, and saves the updated veteran record.
     */
    @Test
    void updateServiceInformationSavesValidChanges() {

        // Return the test user when the repository is queried.
        when(userRepository.findByUsername("testveteran"))
                .thenReturn(user);

        // Return the veteran profile that will be updated.
        when(veteranRepository.findByUserId(1L))
                .thenReturn(veteran);

        // Submit valid military service information through the controller.
        String result = profileController.updateServiceInformation(
                authentication,
                "United States Army",
                "2020-01-01",
                "2022-01-01",
                "Honorable",
                model
        );

        // Verify that a successful update redirects to the profile page.
        assertEquals("redirect:/my-profile", result);

        // Verify that the branch of service was updated.
        assertEquals(
                "United States Army",
                veteran.getBranchOfService()
        );

        // Verify that the service start date was parsed and stored correctly.
        assertEquals(
                LocalDate.parse("2020-01-01"),
                veteran.getServiceStartDate()
        );

        // Verify that the service end date was parsed and stored correctly.
        assertEquals(
                LocalDate.parse("2022-01-01"),
                veteran.getServiceEndDate()
        );

        // Verify that the discharge status was updated.
        assertEquals("Honorable", veteran.getDischargeStatus());

        // Verify that the updated veteran record was saved.
        verify(veteranRepository).save(veteran);
    }

    /**
     * Verifies that service information is rejected when the end date
     * occurs before the start date.
     *
     * Checks that the edit form is returned with an error message and
     * that the invalid record is not saved.
     */
    @Test
    void updateRejectsEndDateBeforeStartDate() {

        // Return the test user when the repository is queried.
        when(userRepository.findByUsername("testveteran"))
                .thenReturn(user);

        // Return the veteran profile being updated.
        when(veteranRepository.findByUserId(1L))
                .thenReturn(veteran);

        // Submit service dates in the wrong chronological order.
        String result = profileController.updateServiceInformation(
                authentication,
                "United States Army",
                "2022-01-01",
                "2020-01-01",
                "Honorable",
                model
        );

        // Verify that the controller returns the edit form.
        assertEquals("edit-service-info", result);

        // Verify that the expected validation error is added to the model.
        assertEquals(
                "Service end date cannot be before service start date.",
                model.getAttribute("errorMessage")
        );

        // Verify that the invalid service information is not saved.
        verify(veteranRepository, never()).save(veteran);
    }

    /**
     * Verifies that service dates in the future are rejected.
     *
     * The test submits tomorrow's date for both service dates and checks
     * that the controller returns the edit form with a validation message.
     */
    @Test
    void updateRejectsFutureServiceDates() {

        // Return the test user when the repository is queried.
        when(userRepository.findByUsername("testveteran"))
                .thenReturn(user);

        // Return the veteran profile being updated.
        when(veteranRepository.findByUserId(1L))
                .thenReturn(veteran);

        // Generate a date one day after the current date.
        String futureDate = LocalDate.now().plusDays(1).toString();

        // Submit the future dates through the controller.
        String result = profileController.updateServiceInformation(
                authentication,
                "United States Army",
                futureDate,
                futureDate,
                "Honorable",
                model
        );

        // Verify that the controller returns the edit form.
        assertEquals("edit-service-info", result);

        // Verify that the expected future-date validation error is displayed.
        assertEquals(
                "Service dates cannot be in the future.",
                model.getAttribute("errorMessage")
        );

        // Verify that invalid service information is not saved.
        verify(veteranRepository, never()).save(veteran);
    }

    /**
     * Verifies that service information is rejected when a date
     * cannot be parsed.
     *
     * Checks that the controller returns the edit form with an error
     * message and does not save the invalid information.
     */
    @Test
    void updateRejectsInvalidDateFormat() {

        // Return the test user when the repository is queried.
        when(userRepository.findByUsername("testveteran"))
                .thenReturn(user);

        // Return the veteran profile being updated.
        when(veteranRepository.findByUserId(1L))
                .thenReturn(veteran);

        // Submit an invalid start date and a valid end date.
        String result = profileController.updateServiceInformation(
                authentication,
                "United States Army",
                "not-a-date",
                "2022-01-01",
                "Honorable",
                model
        );

        // Verify that the controller returns the edit form.
        assertEquals("edit-service-info", result);

        // Verify that the expected date-format error is displayed.
        assertEquals(
                "Please enter valid service start and end dates.",
                model.getAttribute("errorMessage")
        );

        // Verify that invalid service information is not saved.
        verify(veteranRepository, never()).save(veteran);
    }

    /**
     * Verifies that service information cannot be updated by an unknown user.
     *
     * The controller is expected to throw AccessDeniedException and
     * must not save a veteran record.
     */
    @Test
    void updateRejectsUnknownUser() {

        // Simulate a repository lookup that finds no matching user.
        when(userRepository.findByUsername("testveteran"))
                .thenReturn(null);

        // Verify that the update attempt throws an access-denied exception.
        assertThrows(
                AccessDeniedException.class,
                () -> profileController.updateServiceInformation(
                        authentication,
                        "United States Army",
                        "2020-01-01",
                        "2022-01-01",
                        "Honorable",
                        model
                )
        );

        // Verify that no veteran record is saved.
        verify(veteranRepository, never()).save(veteran);
    }

    /**
     * Verifies that service information cannot be updated when the veteran
     * profile is missing.
     *
     * The controller is expected to throw AccessDeniedException and
     * must not save a veteran record.
     */
    @Test
    void updateRejectsMissingVeteranProfile() {

        // Return the test user when the repository is queried.
        when(userRepository.findByUsername("testveteran"))
                .thenReturn(user);

        // Simulate a missing veteran profile.
        when(veteranRepository.findByUserId(1L))
                .thenReturn(null);

        // Verify that the update attempt throws an access-denied exception.
        assertThrows(
                AccessDeniedException.class,
                () -> profileController.updateServiceInformation(
                        authentication,
                        "United States Army",
                        "2020-01-01",
                        "2022-01-01",
                        "Honorable",
                        model
                )
        );

        // Verify that no veteran record is saved.
        verify(veteranRepository, never()).save(veteran);
    }
}