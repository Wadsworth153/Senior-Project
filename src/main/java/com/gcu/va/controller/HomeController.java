package com.gcu.va.controller;

// Import the User entity representing an application account.
import com.gcu.va.entity.User;

// Import the Veteran entity containing personal and military service information.
import com.gcu.va.entity.Veteran;

// Import the repository used to retrieve user accounts.
import com.gcu.va.repository.UserRepository;

// Import the service responsible for calculating simulated eligibility results.
import com.gcu.va.service.EligibilityService;

// Import the exception used when a user cannot access a requested resource.
import org.springframework.security.access.AccessDeniedException;

// Import Authentication to identify the currently logged-in user.
import org.springframework.security.core.Authentication;

// Import Spring's Controller annotation for handling web requests.
import org.springframework.stereotype.Controller;

// Import Model to pass information from the controller to the view.
import org.springframework.ui.Model;

// Import GetMapping to map HTTP GET requests to controller methods.
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Handles requests for the application's home dashboard.
 *
 * This controller retrieves the authenticated user's account and associated
 * veteran profile, calculates the prototype's simulated eligibility status
 * and explanation, and supplies the results to the home page.
 */
@Controller
public class HomeController {

    // Repository used to retrieve the account of the authenticated user.
    private final UserRepository userRepository;

    // Service used to calculate eligibility status and its explanation.
    private final EligibilityService eligibilityService;

    /**
     * Constructs the home controller with its required dependencies.
     *
     * Spring supplies the repository and eligibility service through
     * constructor-based dependency injection.
     *
     * @param userRepository repository used to retrieve user accounts
     * @param eligibilityService service used to evaluate simulated eligibility
     */
    public HomeController(
            UserRepository userRepository,
            EligibilityService eligibilityService) {

        // Store the injected user repository.
        this.userRepository = userRepository;

        // Store the injected eligibility service.
        this.eligibilityService = eligibilityService;
    }

    /**
     * Displays the home dashboard for the authenticated user.
     *
     * The method retrieves the user's account, obtains the associated
     * veteran profile, calculates the simulated eligibility status and
     * reason, and adds the resulting information to the view model.
     *
     * @param authentication authentication information for the current user
     * @param model model used to provide information to the home page
     * @return the home page view name
     * @throws AccessDeniedException if no matching user account is found
     */
    @GetMapping("/home")
    public String home(Authentication authentication, Model model) {

        // Retrieve the account using the authenticated user's username.
        User user = userRepository.findByUsername(authentication.getName());

        // Prevent access when the authenticated username has no matching account.
        if (user == null) {
            throw new AccessDeniedException("User account not found.");
        }

        // Retrieve the veteran profile associated with the user account.
        // The profile may be null if no veteran profile is associated.
        Veteran veteran = user.getVeteran();

        // Calculate the simulated eligibility status using the veteran profile.
        String status = eligibilityService.calculateStatus(veteran);

        // Generate an explanation for the simulated eligibility result.
        String reason = eligibilityService.calculateReason(veteran);

        // Add the user account to the model for use by the home page.
        model.addAttribute("user", user);

        // Add the veteran profile to the model.
        model.addAttribute("veteran", veteran);

        // Provide the eligibility status to the view.
        model.addAttribute("eligibilityStatus", status);

        // Provide the explanation for the eligibility result to the view.
        model.addAttribute("eligibilityReason", reason);

        // Return the Thymeleaf home template for rendering.
        return "home";
    }
}