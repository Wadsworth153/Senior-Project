package com.gcu.va.controller;

// Import LocalDate to parse, compare, and validate military service dates.
import java.time.LocalDate;

// Import the exception used when a user is not authorized to access a resource.
import org.springframework.security.access.AccessDeniedException;

// Import Authentication to identify the currently logged-in user.
import org.springframework.security.core.Authentication;

// Import Spring's Controller annotation for handling web requests.
import org.springframework.stereotype.Controller;

// Import Model to pass profile information and validation errors to the view.
import org.springframework.ui.Model;

// Import annotations used to map HTTP GET and POST requests.
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

// Import the User and Veteran entities used by the application.
import com.gcu.va.entity.User;
import com.gcu.va.entity.Veteran;

// Import repositories used to retrieve and save user and veteran records.
import com.gcu.va.repository.UserRepository;
import com.gcu.va.repository.VeteranRepository;

// Import the service used to calculate simulated eligibility results.
import com.gcu.va.service.EligibilityService;

/**
 * Handles requests for viewing and updating veteran profile information.
 *
 * This controller displays a veteran's profile and simulated eligibility
 * results, provides a form for editing military service information,
 * and validates submitted information before saving profile changes.
 */
@Controller
public class ProfileController {

    // Repository used to retrieve user accounts.
    private final UserRepository userRepository;

    // Repository used to retrieve and save veteran profiles.
    private final VeteranRepository veteranRepository;

    // Service used to calculate eligibility status and its explanation.
    private final EligibilityService eligibilityService;

    /**
     * Constructs the profile controller with its required dependencies.
     *
     * Spring supplies the repositories and eligibility service through
     * constructor-based dependency injection.
     *
     * @param userRepository repository used to retrieve user accounts
     * @param veteranRepository repository used to access veteran profiles
     * @param eligibilityService service used to evaluate simulated eligibility
     */
    public ProfileController(
            UserRepository userRepository,
            VeteranRepository veteranRepository,
            EligibilityService eligibilityService) {

        // Store the injected user repository.
        this.userRepository = userRepository;

        // Store the injected veteran repository.
        this.veteranRepository = veteranRepository;

        // Store the injected eligibility service.
        this.eligibilityService = eligibilityService;
    }

    /**
     * Displays the authenticated veteran's profile and eligibility results.
     *
     * The method retrieves the current user's account and associated veteran
     * profile, calculates the prototype's simulated eligibility status and
     * explanation, and supplies this information to the profile page.
     *
     * @param auth authentication information for the current user
     * @param model model used to provide information to the profile page
     * @return the veteran profile page view name
     * @throws AccessDeniedException if the user account cannot be found
     */
    @GetMapping("/my-profile")
    public String myProfile(Authentication auth, Model model) {

        // Retrieve the account associated with the authenticated username.
        User user = userRepository.findByUsername(auth.getName());

        // Deny access if no matching account exists.
        if (user == null) {
            throw new AccessDeniedException("User account not found.");
        }

        // Retrieve the veteran profile associated with the user's ID.
        Veteran veteran = veteranRepository.findByUserId(user.getId());

        // Add the user account to the model for display by the view.
        model.addAttribute("user", user);

        // Add the veteran profile to the model.
        model.addAttribute("veteran", veteran);

        // Calculate and provide the simulated eligibility status.
        model.addAttribute(
                "eligibilityStatus",
                eligibilityService.calculateStatus(veteran));

        // Calculate and provide the explanation for the eligibility result.
        model.addAttribute(
                "eligibilityReason",
                eligibilityService.calculateReason(veteran));

        // Return the profile template for rendering.
        return "my-profile";
    }

    /**
     * Displays the form used to edit military service information.
     *
     * The method retrieves the authenticated user's account and associated
     * veteran profile. A profile is required to access the editing form.
     *
     * @param auth authentication information for the current user
     * @param model model used to provide the veteran profile to the form
     * @return the service information editing page view name
     * @throws AccessDeniedException if the account or veteran profile
     *                                cannot be found
     */
    @GetMapping("/my-profile/edit")
    public String showEditForm(Authentication auth, Model model) {

        // Retrieve the account associated with the authenticated username.
        User user = userRepository.findByUsername(auth.getName());

        // Deny access if no matching user account exists.
        if (user == null) {
            throw new AccessDeniedException("User account not found.");
        }

        // Retrieve the veteran profile associated with the user's ID.
        Veteran veteran = veteranRepository.findByUserId(user.getId());

        // Deny access if the account has no associated veteran profile.
        if (veteran == null) {
            throw new AccessDeniedException(
                    "No veteran profile is associated with this account.");
        }

        // Supply the existing profile information to populate the edit form.
        model.addAttribute("veteran", veteran);

        // Return the service information editing template.
        return "edit-service-info";
    }

    /**
     * Validates and saves updated military service information.
     *
     * The method retrieves the authenticated user's veteran profile,
     * parses the submitted dates, validates required fields and date ranges,
     * and saves the permitted changes when all checks pass.
     *
     * If validation fails, the editing form is redisplayed with an
     * explanatory error message and the existing veteran profile.
     *
     * @param auth authentication information for the current user
     * @param branchOfService submitted military service branch
     * @param serviceStartDate submitted service start date in ISO date format
     * @param serviceEndDate submitted service end date in ISO date format
     * @param dischargeStatus submitted discharge status
     * @param model model used to provide the profile and validation errors
     * @return the editing page when validation fails, or a redirect to
     *         the profile page after a successful update
     * @throws AccessDeniedException if the user account or veteran profile
     *                                cannot be found
     */
    @PostMapping("/my-profile/edit")
    public String updateServiceInformation(
            Authentication auth,
            @RequestParam String branchOfService,
            @RequestParam String serviceStartDate,
            @RequestParam String serviceEndDate,
            @RequestParam String dischargeStatus,
            Model model) {

        // Retrieve the account associated with the authenticated username.
        User user = userRepository.findByUsername(auth.getName());

        // Deny access if no matching user account exists.
        if (user == null) {
            throw new AccessDeniedException("User account not found.");
        }

        // Retrieve the veteran profile associated with the user's ID.
        Veteran veteran = veteranRepository.findByUserId(user.getId());

        // Deny access if no veteran profile is associated with the account.
        if (veteran == null) {
            throw new AccessDeniedException(
                    "No veteran profile is associated with this account.");
        }

        // Declare variables that will hold the parsed service dates.
        LocalDate startDate;
        LocalDate endDate;

        try {
            // Convert the submitted date strings into LocalDate objects.
            startDate = LocalDate.parse(serviceStartDate);
            endDate = LocalDate.parse(serviceEndDate);

        } catch (RuntimeException ex) {

            // Report invalid or unparseable date input to the user.
            model.addAttribute(
                    "errorMessage",
                    "Please enter valid service start and end dates.");

            // Restore the existing profile information for the editing form.
            model.addAttribute("veteran", veteran);

            // Redisplay the form without saving the invalid information.
            return "edit-service-info";
        }

        // Retrieve today's date to check whether either service date is future-dated.
        LocalDate today = LocalDate.now();

        // Ensure a military service branch has been provided.
        if (branchOfService == null || branchOfService.isBlank()) {
            model.addAttribute(
                    "errorMessage",
                    "Branch of service is required.");

            // Preserve the existing profile information for redisplay.
            model.addAttribute("veteran", veteran);

            // Return to the editing form because validation failed.
            return "edit-service-info";
        }

        // Ensure a discharge status has been provided.
        if (dischargeStatus == null || dischargeStatus.isBlank()) {
            model.addAttribute(
                    "errorMessage",
                    "Discharge status is required.");

            // Preserve the existing profile information for redisplay.
            model.addAttribute("veteran", veteran);

            // Return to the editing form because validation failed.
            return "edit-service-info";
        }

        // Reject a service end date that occurs before the start date.
        if (endDate.isBefore(startDate)) {
            model.addAttribute(
                    "errorMessage",
                    "Service end date cannot be before service start date.");

            // Preserve the existing profile information for redisplay.
            model.addAttribute("veteran", veteran);

            // Return to the editing form without saving invalid dates.
            return "edit-service-info";
        }

        // Reject service dates that occur in the future.
        if (startDate.isAfter(today) || endDate.isAfter(today)) {
            model.addAttribute(
                    "errorMessage",
                    "Service dates cannot be in the future.");

            // Preserve the existing profile information for redisplay.
            model.addAttribute("veteran", veteran);

            // Return to the editing form without saving invalid dates.
            return "edit-service-info";
        }

        // Update only the profile fields permitted by this form.
        // Trim surrounding whitespace from the submitted service branch.
        veteran.setBranchOfService(branchOfService.trim());

        // Store the validated service start date.
        veteran.setServiceStartDate(startDate);

        // Store the validated service end date.
        veteran.setServiceEndDate(endDate);

        // Store the submitted discharge status.
        veteran.setDischargeStatus(dischargeStatus);

        // Persist the updated veteran profile in the database.
        veteranRepository.save(veteran);

        // Redirect to the profile page after a successful update.
        return "redirect:/my-profile";
    }
}