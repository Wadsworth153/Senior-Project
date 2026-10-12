package com.gcu.va.controller;

// Import LocalDate to record the date a claim is submitted.
import java.time.LocalDate;

// Import entities used to create and manage claims and identify user roles.
import com.gcu.va.entity.Claim;
import com.gcu.va.entity.ClaimStatus;
import com.gcu.va.entity.Role;
import com.gcu.va.entity.User;

// Import the service responsible for claim-related operations.
import com.gcu.va.service.ClaimService;

// Import the repository used to retrieve the authenticated user's account.
import com.gcu.va.repository.UserRepository;

// Import an exception used when a user is not authorized to perform an action.
import org.springframework.security.access.AccessDeniedException;

// Import Authentication to access the currently authenticated user's identity.
import org.springframework.security.core.Authentication;

// Import Spring MVC annotations and classes for handling web requests and views.
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Handles web requests related to veteran claims.
 *
 * This controller allows authorized users to view claims, allows veterans
 * with associated profiles to submit claims, and restricts claim-status
 * updates to administrators.
 */
@Controller
public class ClaimController {

    // Service used to retrieve and save claim records.
    private final ClaimService claimService;

    // Repository used to retrieve the currently authenticated user's account.
    private final UserRepository userRepository;

    /**
     * Constructs the controller with its required service and repository.
     *
     * Spring supplies these dependencies through constructor-based
     * dependency injection.
     *
     * @param claimService service used to manage claim records
     * @param userRepository repository used to retrieve user accounts
     */
    public ClaimController(
            ClaimService claimService,
            UserRepository userRepository) {

        // Store the injected claim service.
        this.claimService = claimService;

        // Store the injected user repository.
        this.userRepository = userRepository;
    }

    /**
     * Displays claims available to the currently authenticated user.
     *
     * Administrators receive all claims. Veterans with an associated
     * veteran profile receive only their own claims.
     *
     * @param auth authentication information for the current user
     * @param model model used to provide claim data to the view
     * @return the claims page view name
     * @throws AccessDeniedException if the user account or veteran profile
     *                                required for access cannot be found
     */
    @GetMapping("/claims")
    public String viewClaims(Authentication auth, Model model) {

        // Retrieve the account associated with the authenticated username.
        User user = userRepository.findByUsername(auth.getName());

        // Deny access if the authenticated username has no matching account.
        if (user == null) {
            throw new AccessDeniedException("User account not found.");
        }

        // Administrators are allowed to view all claim records.
        if (user.getRole() == Role.ROLE_ADMIN) {
            model.addAttribute("claims", claimService.getAllClaims());

        // Veterans with an associated profile can view their own claims.
        } else if (user.getRole() == Role.ROLE_VETERAN
                && user.getVeteran() != null) {

            // Retrieve claims using the veteran profile's unique identifier.
            model.addAttribute(
                "claims",
                claimService.getClaimsForVeteran(user.getVeteran().getId())
            );

        // Deny access when the account does not meet either permitted condition.
        } else {
            throw new AccessDeniedException(
                "No veteran profile is associated with this account."
            );
        }

        // Render the claims page with the claims added to the model.
        return "claims";
    }

    /**
     * Displays the form used by veterans to create a claim.
     *
     * Only users with the veteran role and an associated veteran profile
     * are permitted to access this form.
     *
     * @param auth authentication information for the current user
     * @param model model used to provide an empty claim to the form
     * @return the claim creation page view name
     * @throws AccessDeniedException if the user is not an eligible
     *                                veteran account
     */
    @GetMapping("/claims/new")
    public String showCreateForm(Authentication auth, Model model) {

        // Retrieve the account associated with the authenticated username.
        User user = userRepository.findByUsername(auth.getName());

        // Ensure the account exists, has the veteran role, and has a profile.
        if (user == null || user.getRole() != Role.ROLE_VETERAN
                || user.getVeteran() == null) {
            throw new AccessDeniedException(
                "Only veterans with a profile can submit claims."
            );
        }

        // Provide an empty Claim object for the form to bind its input fields.
        model.addAttribute("claim", new Claim());

        // Render the claim creation form.
        return "create-claim";
    }

    /**
     * Processes a submitted claim creation form.
     *
     * The new claim is associated with the authenticated veteran, assigned
     * an initial SUBMITTED status, and given the current submission date.
     *
     * @param claimType type of claim being submitted
     * @param description optional description of the claim
     * @param auth authentication information for the current user
     * @return redirect instruction to the claims page
     * @throws AccessDeniedException if the user is not an eligible
     *                                veteran account
     */
    @PostMapping("/claims/new")
    public String createClaim(
            @RequestParam String claimType,
            @RequestParam(required = false) String description,
            Authentication auth) {

        // Retrieve the account associated with the authenticated username.
        User user = userRepository.findByUsername(auth.getName());

        // Only veterans with an associated profile can submit claims.
        if (user == null || user.getRole() != Role.ROLE_VETERAN
                || user.getVeteran() == null) {
            throw new AccessDeniedException(
                "Only veterans with a profile can submit claims."
            );
        }

        // Create a new claim entity to hold the submitted information.
        Claim claim = new Claim();

        // Store the claim type provided by the user.
        claim.setClaimType(claimType);

        // Store the optional claim description.
        claim.setDescription(description);

        // Associate the claim with the authenticated veteran's profile.
        claim.setVeteran(user.getVeteran());

        // Set the initial status for a newly submitted claim.
        claim.setStatus(ClaimStatus.SUBMITTED);

        // Record the date on which the claim is submitted.
        claim.setSubmittedDate(LocalDate.now());

        // Save the new claim through the claim service.
        claimService.saveClaim(claim);

        // Redirect to the claims page after successfully saving the claim.
        return "redirect:/claims";
    }

    /**
     * Updates the status of an existing claim.
     *
     * Only administrators can perform this operation. The supplied status
     * string must match a value defined in the ClaimStatus enumeration.
     *
     * @param id unique identifier of the claim to update
     * @param status requested claim status
     * @param auth authentication information for the current user
     * @return redirect instruction to the administrator dashboard
     * @throws AccessDeniedException if the account is not an administrator
     * @throws IllegalArgumentException if the supplied status is invalid
     */
    @PostMapping("/claims/update")
    public String updateClaimStatus(
            @RequestParam Long id,
            @RequestParam String status,
            Authentication auth) {

        // Retrieve the account associated with the authenticated username.
        User user = userRepository.findByUsername(auth.getName());

        // Restrict claim-status updates to administrator accounts.
        if (user == null || user.getRole() != Role.ROLE_ADMIN) {
            throw new AccessDeniedException(
                "Only administrators can update claim statuses."
            );
        }

        // Retrieve the claim that the administrator wants to update.
        Claim claim = claimService.getById(id);

        try {
            // Convert the submitted status string into a ClaimStatus enum value.
            ClaimStatus newStatus = ClaimStatus.valueOf(status);

            // Apply the validated status to the claim.
            claim.setStatus(newStatus);

        } catch (IllegalArgumentException ex) {

            // Report invalid status values while preserving the original cause.
            throw new IllegalArgumentException("Invalid claim status.", ex);
        }

        // Save the updated claim record.
        claimService.saveClaim(claim);

        // Return the administrator to the dashboard after the update.
        return "redirect:/admin";
    }
}