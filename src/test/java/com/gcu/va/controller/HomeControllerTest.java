package com.gcu.va.controller;

// Import JUnit assertions used to verify expected test results.
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

// Import Mockito methods for verifying interactions and configuring mock behavior.
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Import the entities used to represent the test user and veteran.
import com.gcu.va.entity.Role;
import com.gcu.va.entity.User;
import com.gcu.va.entity.Veteran;

// Import the repository and service dependencies used by HomeController.
import com.gcu.va.repository.UserRepository;
import com.gcu.va.service.EligibilityService;

// Import JUnit testing annotations and Mockito integration.
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

// Import Spring MVC model types for checking dashboard attributes.
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

/**
 * Tests the dashboard functionality implemented by HomeController.
 *
 * These tests verify that the dashboard displays user and eligibility
 * information, handles a missing veteran profile, and rejects requests
 * when the authenticated username does not correspond to an existing user.
 *
 * Mockito provides simulated dependencies so the controller can be tested
 * without requiring actual database operations.
 */
@ExtendWith(MockitoExtension.class)
class HomeControllerTest {

    // Mock the repository used to retrieve user accounts.
    @Mock
    private UserRepository userRepository;

    // Mock the service responsible for calculating eligibility information.
    @Mock
    private EligibilityService eligibilityService;

    // Mock authentication information for the current user.
    @Mock
    private Authentication authentication;

    // Create the controller and inject its mocked dependencies.
    @InjectMocks
    private HomeController homeController;

    // Store the test user account.
    private User user;

    // Store the veteran profile associated with the test user.
    private Veteran veteran;

    // Store model attributes populated by the controller.
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

        // Initialize a model for capturing dashboard attributes.
        model = new ExtendedModelMap();

        // Configure authentication to identify the test user.
        when(authentication.getName()).thenReturn("testveteran");
    }

    /**
     * Verifies that the dashboard displays the user and eligibility information.
     *
     * The test configures the repository and eligibility service to return
     * test data, then checks the view name and the attributes added to the model.
     */
    @Test
    void dashboardDisplaysUserAndEligibilityInformation() {

        // Return the test user when the repository is queried.
        when(userRepository.findByUsername("testveteran"))
                .thenReturn(user);

        // Configure the eligibility service to return an eligible status.
        when(eligibilityService.calculateStatus(veteran))
                .thenReturn("Eligible");

        // Configure the eligibility service to return an explanation.
        when(eligibilityService.calculateReason(veteran))
                .thenReturn(
                        "Meets the prototype's service duration and discharge requirements."
                );

        // Invoke the dashboard controller method.
        String result = homeController.home(authentication, model);

        // Verify that the controller returns the home template.
        assertEquals("home", result);

        // Verify that the model contains the user account.
        assertEquals(user, model.getAttribute("user"));

        // Verify that the model contains the veteran profile.
        assertEquals(veteran, model.getAttribute("veteran"));

        // Verify that the eligibility status is displayed in the model.
        assertEquals(
                "Eligible",
                model.getAttribute("eligibilityStatus")
        );

        // Verify that the eligibility explanation is displayed in the model.
        assertEquals(
                "Meets the prototype's service duration and discharge requirements.",
                model.getAttribute("eligibilityReason")
        );

        // Verify that the controller retrieved the correct user.
        verify(userRepository).findByUsername("testveteran");

        // Verify that the eligibility status was calculated for the veteran.
        verify(eligibilityService).calculateStatus(veteran);

        // Verify that the eligibility explanation was calculated for the veteran.
        verify(eligibilityService).calculateReason(veteran);
    }

    /**
     * Verifies that the dashboard handles a user without a veteran profile.
     *
     * The test checks that the dashboard still loads and displays the
     * eligibility information returned by the service for a missing profile.
     */
    @Test
    void dashboardHandlesMissingVeteranProfile() {

        // Remove the veteran profile from the test user.
        user.setVeteran(null);

        // Return the user account without an associated veteran profile.
        when(userRepository.findByUsername("testveteran"))
                .thenReturn(user);

        // Configure the eligibility service to return an ineligible status.
        when(eligibilityService.calculateStatus(null))
                .thenReturn("Ineligible");

        // Configure the service to explain that the profile is missing.
        when(eligibilityService.calculateReason(null))
                .thenReturn("No veteran profile is available.");

        // Invoke the dashboard controller method.
        String result = homeController.home(authentication, model);

        // Verify that the controller returns the home template.
        assertEquals("home", result);

        // Verify that the model still contains the user account.
        assertEquals(user, model.getAttribute("user"));

        // Verify that the model has no veteran profile.
        assertNull(model.getAttribute("veteran"));

        // Verify that the returned eligibility status is included in the model.
        assertEquals(
                "Ineligible",
                model.getAttribute("eligibilityStatus")
        );

        // Verify that the missing-profile explanation is included in the model.
        assertEquals(
                "No veteran profile is available.",
                model.getAttribute("eligibilityReason")
        );
    }

    /**
     * Verifies that the dashboard rejects an unknown user.
     *
     * When the repository cannot find the authenticated username,
     * the controller is expected to throw AccessDeniedException.
     */
    @Test
    void dashboardRejectsUnknownUser() {

        // Configure the repository to indicate that the user does not exist.
        when(userRepository.findByUsername("testveteran"))
                .thenReturn(null);

        // Verify that attempting to load the dashboard throws an access-denied exception.
        assertThrows(
                AccessDeniedException.class,
                () -> homeController.home(authentication, model)
        );
    }
}