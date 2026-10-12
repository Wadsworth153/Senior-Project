package com.gcu.va.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gcu.va.entity.Goal;
import com.gcu.va.entity.GoalStatus;
import com.gcu.va.entity.GoalType;
import com.gcu.va.entity.Role;
import com.gcu.va.entity.User;
import com.gcu.va.entity.Veteran;
import com.gcu.va.repository.UserRepository;
import com.gcu.va.service.GoalService;

/**
 * Handles web requests for managing a veteran's personal goals.
 *
 * The controller identifies the authenticated user through Spring
 * Security and retrieves the associated veteran record.
 *
 * Goal ownership is determined by the authenticated user's veteran
 * record rather than a veteran ID supplied by the browser.
 */
@Controller
@RequestMapping("/goals")
public class GoalController {

    private final GoalService goalService;
    private final UserRepository userRepository;

    /**
     * Constructs the controller with its required dependencies.
     *
     * @param goalService service for managing goals
     * @param userRepository repository for retrieving user accounts
     */
    public GoalController(
            GoalService goalService,
            UserRepository userRepository) {

        this.goalService = goalService;
        this.userRepository = userRepository;
    }

    /**
     * Displays all goals belonging to the authenticated veteran.
     *
     * @param authentication the current authenticated user
     * @param model attributes supplied to the Thymeleaf template
     * @return the My Goals page
     */
    @GetMapping
    public String listGoals(
            Authentication authentication,
            Model model) {

        // Resolve the veteran associated with the logged-in account.
        Veteran veteran = getAuthenticatedVeteran(authentication);

        // Retrieve only this veteran's goals.
        List<Goal> goals =
                goalService.getGoalsByVeteran(veteran.getId());

        // Supply the goals to the Thymeleaf page.
        model.addAttribute("goals", goals);

        // Display the My Goals page.
        return "goals";
    }

    /**
     * Displays the form used to create a new goal.
     *
     * @param authentication the current authenticated user
     * @param model attributes supplied to the Thymeleaf template
     * @return the goal creation page
     */
    @GetMapping("/new")
    public String showCreateGoalForm(
            Authentication authentication,
            Model model) {

        // Confirm that the current user has an associated veteran record.
        getAuthenticatedVeteran(authentication);

        // Create an empty goal for form binding.
        model.addAttribute("goal", new Goal());

        // Provide the available goal types to the form.
        model.addAttribute("goalTypes", GoalType.values());

        // Display the goal creation form.
        return "create-goal";
    }

    /**
     * Processes the submission of a new goal.
     *
     * @param goal the goal information submitted by the form
     * @param authentication the current authenticated user
     * @param redirectAttributes attributes used for feedback messages
     * @return a redirect to the My Goals page
     */
    @PostMapping
    public String createGoal(
            @ModelAttribute("goal") Goal goal,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        // Identify the veteran using the authenticated account.
        Veteran veteran = getAuthenticatedVeteran(authentication);

        // Let the service validate and save the goal.
        goalService.createGoal(goal, veteran.getId());

        // Display a confirmation after the redirect.
        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Your goal was created successfully.");

        // Return to the veteran's goals.
        return "redirect:/goals";
    }

    /**
     * Displays the form for editing an existing goal.
     *
     * @param id the ID of the goal to edit
     * @param authentication the current authenticated user
     * @param model attributes supplied to the Thymeleaf template
     * @return the goal editing page
     */
    @GetMapping("/{id}/edit")
    public String showEditGoalForm(
            @PathVariable Long id,
            Authentication authentication,
            Model model) {

        // Resolve the authenticated veteran.
        Veteran veteran = getAuthenticatedVeteran(authentication);

        // Retrieve the goal only if the veteran owns it.
        Goal goal = goalService.getGoalByIdAndVeteran(
                id, veteran.getId());

        // Prevent access to missing goals or another veteran's goals.
        if (goal == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Goal not found.");
        }

        // Supply the goal and available statuses to the form.
        model.addAttribute("goal", goal);
        model.addAttribute("goalStatuses", GoalStatus.values());

        // Display the editing form.
        return "edit-goal";
    }

    /**
     * Updates an existing goal owned by the authenticated veteran.
     *
     * @param id the ID of the goal to update
     * @param title the updated goal title
     * @param description the updated description
     * @param status the updated progress status
     * @param authentication the current authenticated user
     * @param redirectAttributes attributes used for feedback messages
     * @return a redirect to the My Goals page
     */
    @PostMapping("/{id}/edit")
    public String updateGoal(
            @PathVariable Long id,
            @RequestParam String title,
            @RequestParam(required = false) String description,
            @RequestParam GoalStatus status,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        // Identify the veteran who is making the request.
        Veteran veteran = getAuthenticatedVeteran(authentication);

        // Update the goal through the ownership-aware service method.
        goalService.updateGoal(
                id,
                veteran.getId(),
                title,
                description,
                status);

        // Display a confirmation message.
        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Your goal was updated successfully.");

        // Return to the My Goals page.
        return "redirect:/goals";
    }

    /**
     * Deletes a goal belonging to the authenticated veteran.
     *
     * This endpoint accepts POST requests rather than GET requests
     * so that a simple page visit cannot trigger a deletion.
     *
     * @param id the ID of the goal to delete
     * @param authentication the current authenticated user
     * @param redirectAttributes attributes used for feedback messages
     * @return a redirect to the My Goals page
     */
    @PostMapping("/{id}/delete")
    public String deleteGoal(
            @PathVariable Long id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        // Identify the authenticated veteran.
        Veteran veteran = getAuthenticatedVeteran(authentication);

        // The service verifies ownership before deleting the goal.
        goalService.deleteGoal(id, veteran.getId());

        // Display a confirmation message.
        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Your goal was deleted successfully.");

        // Return to the My Goals page.
        return "redirect:/goals";
    }

    /**
     * Retrieves the veteran associated with the authenticated account.
     *
     * The method does not accept a user or veteran ID from the request.
     * It uses the username supplied by Spring Security instead.
     *
     * @param authentication the current authenticated user
     * @return the associated veteran record
     * @throws ResponseStatusException if the account or veteran record
     *         cannot be found, or the account is not a veteran account
     */
    private Veteran getAuthenticatedVeteran(
            Authentication authentication) {

        // Reject requests without an authenticated principal.
        if (authentication == null
                || !authentication.isAuthenticated()) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Authentication is required.");
        }

        // Look up the account using the authenticated username.
        User user = userRepository.findByUsername(
                authentication.getName());

        // Reject accounts that cannot be found.
        if (user == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "User account not found.");
        }

        // Only veteran accounts may manage personal goals.
        if (user.getRole() != Role.ROLE_VETERAN) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only veteran accounts can manage personal goals.");
        }

        // Retrieve the veteran associated with the account.
        Veteran veteran = user.getVeteran();

        // Ensure the account has an associated veteran record.
        if (veteran == null || veteran.getId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "No veteran profile is associated with this account.");
        }

        return veteran;
    }
}