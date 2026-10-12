package com.gcu.va.controller;

// Import assertions used to verify test results.
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

// Import Mockito matchers and verification methods.
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Import List for providing test claim collections.
import java.util.List;

// Import the entities and enums used by the claim tests.
import com.gcu.va.entity.Claim;
import com.gcu.va.entity.ClaimStatus;
import com.gcu.va.entity.Role;
import com.gcu.va.entity.User;
import com.gcu.va.entity.Veteran;

// Import the repository and service dependencies used by the controller.
import com.gcu.va.repository.UserRepository;
import com.gcu.va.service.ClaimService;

// Import JUnit testing annotations and Mockito integration.
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

// Import Mockito tools for creating and injecting mock dependencies.
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

// Import the exception expected when a user lacks permission.
import org.springframework.security.access.AccessDeniedException;

// Import Authentication to simulate the currently logged-in user.
import org.springframework.security.core.Authentication;

// Import Spring MVC model types used to verify controller data.
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

/**
 * Tests the claim-management functionality implemented by ClaimController.
 *
 * These tests verify claim viewing, claim submission, claim status updates,
 * and the access restrictions applied to veterans and administrators.
 *
 * Mockito provides simulated dependencies so the controller can be tested
 * without requiring actual database operations.
 */
@ExtendWith(MockitoExtension.class)
class ClaimControllerTest {

    // Mock the service responsible for claim-related operations.
    @Mock
    private ClaimService claimService;

    // Mock the repository used to retrieve user accounts.
    @Mock
    private UserRepository userRepository;

    // Mock the authentication information for the current user.
    @Mock
    private Authentication authentication;

    // Create the controller and inject its mocked dependencies.
    @InjectMocks
    private ClaimController claimController;

    // Store the veteran user used by the test cases.
    private User veteranUser;

    // Store the veteran record associated with the test user.
    private Veteran veteran;

    // Store model attributes populated by controller methods.
    private Model model;

    /**
     * Initializes shared test data before each test executes.
     *
     * Creates a veteran account and associated veteran record, initializes
     * the model, and configures authentication to identify the veteran user.
     */
    @BeforeEach
    void setUp() {

        // Create a test user representing a veteran.
        veteranUser = new User();

        // Assign an identifier to the test user.
        veteranUser.setId(1L);

        // Assign the test username.
        veteranUser.setUsername("testveteran");

        // Assign the veteran role.
        veteranUser.setRole(Role.ROLE_VETERAN);

        // Create a veteran record associated with the test user.
        veteran = new Veteran();

        // Assign an identifier to the veteran record.
        veteran.setId(10L);

        // Set the veteran's test first name.
        veteran.setFirstName("Test");

        // Set the veteran's test last name.
        veteran.setLastName("Veteran");

        // Associate the veteran record with the user account.
        veteran.setUser(veteranUser);

        // Associate the veteran record with the user in the opposite direction.
        veteranUser.setVeteran(veteran);

        // Create a model for storing controller response attributes.
        model = new ExtendedModelMap();

        // Configure the authentication mock to return the test username.
        when(authentication.getName()).thenReturn("testveteran");
    }

    /**
     * Verifies that a veteran can view their own claims.
     *
     * The test checks that the controller returns the claims view,
     * adds claims to the model, and requests claims for the correct veteran.
     */
    @Test
    void veteranCanViewTheirOwnClaims() {

        // Return the test veteran account when the repository is queried.
        when(userRepository.findByUsername("testveteran"))
                .thenReturn(veteranUser);

        // Return a test claim associated with the veteran.
        when(claimService.getClaimsForVeteran(10L))
                .thenReturn(List.of(new Claim()));

        // Invoke the controller method that displays claims.
        String result = claimController.viewClaims(authentication, model);

        // Verify that the controller returns the claims template.
        assertEquals("claims", result);

        // Verify that the model contains the claims attribute.
        assertNotNull(model.getAttribute("claims"));

        // Verify that claims were requested for the correct veteran.
        verify(claimService).getClaimsForVeteran(10L);
    }

    /**
     * Verifies that an administrator can view all claims.
     *
     * The test checks that the administrator receives the claims view
     * and that the controller requests all claims from the service.
     */
    @Test
    void administratorCanViewAllClaims() {

        // Create a test administrator account.
        User admin = new User();

        // Assign the administrator username.
        admin.setUsername("admin");

        // Assign the administrator role.
        admin.setRole(Role.ROLE_ADMIN);

        // Configure authentication to identify the administrator.
        when(authentication.getName()).thenReturn("admin");

        // Return the administrator account from the repository mock.
        when(userRepository.findByUsername("admin")).thenReturn(admin);

        // Return a test claim when all claims are requested.
        when(claimService.getAllClaims()).thenReturn(List.of(new Claim()));

        // Invoke the controller method that displays claims.
        String result = claimController.viewClaims(authentication, model);

        // Verify that the controller returns the claims template.
        assertEquals("claims", result);

        // Verify that the model contains the claims attribute.
        assertNotNull(model.getAttribute("claims"));

        // Verify that the service was asked to retrieve all claims.
        verify(claimService).getAllClaims();
    }

    /**
     * Verifies that a veteran can submit a claim.
     *
     * The test checks the redirect destination and captures the claim
     * passed to the service to verify its type, description, status,
     * associated veteran, and submission date.
     */
    @Test
    void veteranCanSubmitClaim() {

        // Return the test veteran account for the authenticated username.
        when(userRepository.findByUsername("testveteran"))
                .thenReturn(veteranUser);

        // Submit a claim through the controller.
        String result = claimController.createClaim(
                "Disability",
                "Test disability claim",
                authentication
        );

        // Verify that the user is redirected to the claims page.
        assertEquals("redirect:/claims", result);

        // Create an argument captor to inspect the claim passed to the service.
        ArgumentCaptor<Claim> captor =
                ArgumentCaptor.forClass(Claim.class);

        // Capture the claim supplied to the service's save method.
        verify(claimService).saveClaim(captor.capture());

        // Retrieve the captured claim for verification.
        Claim savedClaim = captor.getValue();

        // Verify the submitted claim type.
        assertEquals("Disability", savedClaim.getClaimType());

        // Verify the submitted claim description.
        assertEquals("Test disability claim", savedClaim.getDescription());

        // Verify that the claim starts with SUBMITTED status.
        assertEquals(ClaimStatus.SUBMITTED, savedClaim.getStatus());

        // Verify that the claim is associated with the correct veteran.
        assertEquals(veteran, savedClaim.getVeteran());

        // Verify that the claim has a submission date.
        assertNotNull(savedClaim.getSubmittedDate());
    }

    /**
     * Verifies that an administrator cannot submit a veteran claim.
     *
     * The controller is expected to throw AccessDeniedException when
     * an administrator attempts to use the veteran claim-submission method.
     */
    @Test
    void administratorCannotSubmitVeteranClaim() {

        // Create a test administrator account.
        User admin = new User();

        // Assign the administrator username.
        admin.setUsername("admin");

        // Assign the administrator role.
        admin.setRole(Role.ROLE_ADMIN);

        // Configure authentication to identify the administrator.
        when(authentication.getName()).thenReturn("admin");

        // Return the administrator account from the repository mock.
        when(userRepository.findByUsername("admin")).thenReturn(admin);

        // Verify that attempting to submit a claim throws an access-denied exception.
        assertThrows(
                AccessDeniedException.class,
                () -> claimController.createClaim(
                        "Disability",
                        "Test claim",
                        authentication
                )
        );
    }

    /**
     * Verifies that an administrator can update a claim's status.
     *
     * The test checks that the claim status changes to APPROVED,
     * the administrator is redirected to the admin page, and the
     * updated claim is passed to the service for saving.
     */
    @Test
    void administratorCanUpdateClaimStatus() {

        // Create a test administrator account.
        User admin = new User();

        // Assign the administrator username.
        admin.setUsername("admin");

        // Assign the administrator role.
        admin.setRole(Role.ROLE_ADMIN);

        // Create a claim with an initial SUBMITTED status.
        Claim claim = new Claim();

        // Assign the claim identifier.
        claim.setId(1L);

        // Set the initial claim status.
        claim.setStatus(ClaimStatus.SUBMITTED);

        // Configure authentication to identify the administrator.
        when(authentication.getName()).thenReturn("admin");

        // Return the administrator account from the repository mock.
        when(userRepository.findByUsername("admin")).thenReturn(admin);

        // Return the existing claim when its identifier is requested.
        when(claimService.getById(1L)).thenReturn(claim);

        // Invoke the controller method to approve the claim.
        String result = claimController.updateClaimStatus(
                1L,
                "APPROVED",
                authentication
        );

        // Verify that the administrator is redirected to the admin page.
        assertEquals("redirect:/admin", result);

        // Verify that the claim status has been updated.
        assertEquals(ClaimStatus.APPROVED, claim.getStatus());

        // Verify that the updated claim is saved through the service.
        verify(claimService).saveClaim(claim);
    }

    /**
     * Verifies that a veteran cannot update a claim's status.
     *
     * The controller is expected to reject the operation with
     * AccessDeniedException because the authenticated user is not an admin.
     */
    @Test
    void veteranCannotUpdateClaimStatus() {

        // Return the veteran account for the authenticated username.
        when(userRepository.findByUsername("testveteran"))
                .thenReturn(veteranUser);

        // Verify that the veteran's attempt to update a claim is rejected.
        assertThrows(
                AccessDeniedException.class,
                () -> claimController.updateClaimStatus(
                        1L,
                        "APPROVED",
                        authentication
                )
        );
    }
}