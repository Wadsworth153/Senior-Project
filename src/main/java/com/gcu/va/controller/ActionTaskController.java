package com.gcu.va.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gcu.va.entity.ActionTask;
import com.gcu.va.entity.Goal;
import com.gcu.va.entity.TaskStatus;
import com.gcu.va.entity.TaskType;
import com.gcu.va.entity.User;
import com.gcu.va.entity.Veteran;
import com.gcu.va.repository.GoalRepository;
import com.gcu.va.repository.UserRepository;
import com.gcu.va.service.ActionTaskService;

/**
 * Handles web requests for the Action Planner.
 *
 * Provides functionality for viewing, creating, editing, and deleting
 * tasks while restricting task access to the authenticated veteran.
 */
@Controller
@RequestMapping("/planner")
public class ActionTaskController {

    private final ActionTaskService actionTaskService;
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;

    /**
     * Constructs the controller with its required services and repositories.
     *
     * @param actionTaskService handles task-related business operations
     * @param userRepository retrieves authenticated application users
     * @param goalRepository retrieves goals belonging to veterans
     */
    public ActionTaskController(
            ActionTaskService actionTaskService,
            UserRepository userRepository,
            GoalRepository goalRepository) {

        this.actionTaskService = actionTaskService;
        this.userRepository = userRepository;
        this.goalRepository = goalRepository;
    }

    /**
     * Displays the authenticated veteran's tasks.
     *
     * An optional task type parameter allows the page to display
     * only action-plan tasks or preparation-checklist tasks.
     *
     * @param taskType optional task type filter
     * @param authentication the current authenticated user
     * @param model the model used by the Thymeleaf template
     * @return the planner page
     */
    @GetMapping
    public String showPlanner(
            @RequestParam(required = false) TaskType taskType,
            Authentication authentication,
            Model model) {

        Veteran veteran = getAuthenticatedVeteran(authentication);

        List<ActionTask> tasks;

        if (taskType == null) {
            tasks = actionTaskService.getTasksByVeteran(veteran.getId());
        } else {
            tasks = actionTaskService.getTasksByVeteranAndType(
                    veteran.getId(), taskType);
        }

        model.addAttribute("tasks", tasks);
        model.addAttribute("selectedTaskType", taskType);

        return "planner";
    }

    /**
     * Displays the form for creating a task.
     *
     * @param authentication the current authenticated user
     * @param model the model used by the form
     * @return the task creation page
     */
    @GetMapping("/new")
    public String showCreateTaskForm(
            Authentication authentication,
            Model model) {

        Veteran veteran = getAuthenticatedVeteran(authentication);

        model.addAttribute("task", new ActionTask());
        addFormAttributes(model, veteran.getId());

        return "create-task";
    }

    /**
     * Saves a new task for the authenticated veteran.
     *
     * The service assigns ownership and sets the initial task status.
     *
     * @param task submitted task information
     * @param goalId optional associated goal ID
     * @param authentication the current authenticated user
     * @param redirectAttributes attributes for displaying feedback
     * @return a redirect to the planner
     */
    @PostMapping
    public String createTask(
            @ModelAttribute("task") ActionTask task,
            @RequestParam(required = false) Long goalId,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        Veteran veteran = getAuthenticatedVeteran(authentication);

        actionTaskService.createTask(
                task, veteran.getId(), goalId);

        redirectAttributes.addFlashAttribute(
                "successMessage", "Task created successfully.");

        return "redirect:/planner";
    }

    /**
     * Displays the editing form for a task owned by the veteran.
     *
     * @param id the task ID
     * @param authentication the current authenticated user
     * @param model the model used by the form
     * @return the task editing page
     */
    @GetMapping("/{id}/edit")
    public String showEditTaskForm(
            @PathVariable Long id,
            Authentication authentication,
            Model model) {

        Veteran veteran = getAuthenticatedVeteran(authentication);

        ActionTask task = actionTaskService.getTaskByIdAndVeteran(
                id, veteran.getId());

        model.addAttribute("task", task);
        model.addAttribute("goalId",
                task.getGoal() == null ? null : task.getGoal().getId());

        addFormAttributes(model, veteran.getId());

        return "edit-task";
    }

    /**
     * Updates a task after verifying ownership.
     *
     * @param id the task ID
     * @param task submitted task information
     * @param goalId optional associated goal ID
     * @param authentication the current authenticated user
     * @param redirectAttributes attributes for displaying feedback
     * @return a redirect to the planner
     */
    @PostMapping("/{id}/edit")
    public String updateTask(
            @PathVariable Long id,
            @ModelAttribute("task") ActionTask task,
            @RequestParam(required = false) Long goalId,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        Veteran veteran = getAuthenticatedVeteran(authentication);

        actionTaskService.updateTask(
                id,
                veteran.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getTargetDate(),
                task.getStatus(),
                task.getTaskType(),
                goalId);

        redirectAttributes.addFlashAttribute(
                "successMessage", "Task updated successfully.");

        return "redirect:/planner";
    }

    /**
     * Deletes a task after verifying that it belongs to the veteran.
     *
     * @param id the task ID
     * @param authentication the current authenticated user
     * @param redirectAttributes attributes for displaying feedback
     * @return a redirect to the planner
     */
    @PostMapping("/{id}/delete")
    public String deleteTask(
            @PathVariable Long id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        Veteran veteran = getAuthenticatedVeteran(authentication);

        actionTaskService.deleteTask(id, veteran.getId());

        redirectAttributes.addFlashAttribute(
                "successMessage", "Task deleted successfully.");

        return "redirect:/planner";
    }

    /**
     * Adds the task types, statuses, and available goals to a form.
     *
     * Only goals owned by the authenticated veteran are included.
     *
     * @param model the model used by the form
     * @param veteranId the authenticated veteran's ID
     */
    private void addFormAttributes(Model model, Long veteranId) {

        List<Goal> goals = goalRepository.findByVeteran_Id(veteranId);

        model.addAttribute("goals", goals);
        model.addAttribute("taskTypes", TaskType.values());
        model.addAttribute("taskStatuses", TaskStatus.values());
    }

    /**
     * Retrieves the authenticated veteran and verifies the user's role.
     *
     * @param authentication the current authentication information
     * @return the authenticated veteran
     * @throws ResponseStatusException if authentication or authorization fails
     */
    private Veteran getAuthenticatedVeteran(
            Authentication authentication) {

        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getName())) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Please log in to access the Action Planner.");
        }

        User user = userRepository.findByUsername(authentication.getName());

        if (user == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "The authenticated user could not be found.");
        }
        
        if (user.getRole() != com.gcu.va.entity.Role.ROLE_VETERAN
                || user.getVeteran() == null) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "The Action Planner is available to veteran accounts.");
        }

        return user.getVeteran();
    }
}