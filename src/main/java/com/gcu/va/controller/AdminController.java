package com.gcu.va.controller;

// Import the Role enum to identify administrator accounts.
import com.gcu.va.entity.Role;

// Import the User entity representing an application account.
import com.gcu.va.entity.User;

// Import the repository used to retrieve user and veteran records.
import com.gcu.va.repository.UserRepository;

// Import the service used to retrieve claim records.
import com.gcu.va.service.ClaimService;

// Import Authentication to identify the currently logged-in user.
import org.springframework.security.core.Authentication;

// Import Spring's Controller annotation for handling web requests.
import org.springframework.stereotype.Controller;

// Import Model to pass information to the administrator dashboard.
import org.springframework.ui.Model;

// Import GetMapping to map HTTP GET requests to controller methods.
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Handles requests for the administrator dashboard.
 *
 * This controller displays veteran and claim information to administrators
 * and allows administrators to search for users by username.
 */
@Controller
public class AdminController {

    // Repository used to retrieve user accounts.
    private final UserRepository userRepository;

    // Service used to retrieve claim records.
    private final ClaimService claimService;

    /**
     * Constructs the administrator controller with its required dependencies.
     *
     * Spring supplies the repository and service through constructor-based
     * dependency injection.
     *
     * @param userRepository repository used to retrieve user information
     * @param claimService service used to retrieve claim information
     */
    public AdminController(
            UserRepository userRepository,
            ClaimService claimService) {

        // Store the injected user repository.
        this.userRepository = userRepository;

        // Store the injected claim service.
        this.claimService = claimService;
    }

    /**
     * Displays the administrator dashboard.
     *
     * The method retrieves the authenticated user's account, verifies that
     * the account has the administrator role, and provides user and claim
     * information to the dashboard.
     *
     * @param auth authentication information for the current user
     * @param model model used to provide dashboard information to the view
     * @return the administrator dashboard view, or a redirect to the home page
     *         when the account does not have the administrator role
     */
    @GetMapping("/admin")
    public String adminDashboard(Authentication auth, Model model) {

        // Retrieve the account associated with the authenticated username.
        User user = userRepository.findByUsername(auth.getName());

        // Redirect users who do not have the administrator role.
        if (user.getRole() != Role.ROLE_ADMIN) {
            return "redirect:/home";
        }

        // Add the retrieved user records to the dashboard model.
        model.addAttribute("veterans", userRepository.findAll());

        // Add all claim records to the dashboard model.
        model.addAttribute("claims", claimService.getAllClaims());

        // Return the administrator dashboard template.
        return "admin-dashboard";
    }

    /**
     * Searches for user accounts by username.
     *
     * The method verifies the administrator role before retrieving accounts
     * whose usernames contain the supplied search text. It also supplies
     * all claim records to the dashboard.
     *
     * @param username text used to search usernames
     * @param model model used to provide search results and claims
     * @param auth authentication information for the current user
     * @return the administrator dashboard view, or a redirect to the home page
     *         when the account does not have the administrator role
     */
    @GetMapping("/admin/search")
    public String searchVeterans(
            String username,
            Model model,
            Authentication auth) {

        // Retrieve the account associated with the authenticated username.
        User admin = userRepository.findByUsername(auth.getName());

        // Redirect users who do not have the administrator role.
        if (admin.getRole() != Role.ROLE_ADMIN) {
            return "redirect:/home";
        }

        // Search for accounts whose usernames contain the supplied text.
        model.addAttribute(
            "users",
            userRepository.findByUsernameContaining(username)
        );

        // Include all claim records in the dashboard model.
        model.addAttribute("claims", claimService.getAllClaims());

        // Return the dashboard template with the search results.
        return "admin-dashboard";
    }
}