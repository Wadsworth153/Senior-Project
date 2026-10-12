package com.gcu.va.controller;

// Import the DTO containing the information submitted during registration.
import com.gcu.va.dto.RegistrationForm;

// Import the service responsible for registering veteran accounts.
import com.gcu.va.service.RegistrationService;

// Import LocalDate to validate military service dates.
import java.time.LocalDate;

// Import Valid to trigger validation rules defined on the registration form.
import jakarta.validation.Valid;

// Import Spring's Controller annotation to handle web requests.
import org.springframework.stereotype.Controller;

// Import Model to pass information and error messages to the view.
import org.springframework.ui.Model;

// Import BindingResult to access validation errors.
import org.springframework.validation.BindingResult;

// Import annotations used to map web requests and bind form data.
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Handles requests related to veteran account registration.
 *
 * This controller displays the registration form, validates submitted
 * registration information, checks military service dates and password
 * confirmation, and delegates account creation to RegistrationService.
 */
@Controller
public class RegistrationController {

    // Service responsible for creating user accounts and veteran profiles.
    private final RegistrationService registrationService;

    /**
     * Constructs the registration controller with its required service.
     *
     * Spring supplies the registration service through constructor-based
     * dependency injection.
     *
     * @param registrationService service used to register veteran accounts
     */
    public RegistrationController(RegistrationService registrationService) {

        // Store the injected registration service.
        this.registrationService = registrationService;
    }

    /**
     * Displays a new registration form.
     *
     * An empty RegistrationForm object is added to the model so the
     * registration page can bind its input fields to the form.
     *
     * @param model model used to provide form data to the view
     * @return the registration page view name
     */
    @GetMapping("/register")
    public String showRegistrationPage(Model model) {

        // Add an empty registration form for the user to complete.
        model.addAttribute("form", new RegistrationForm());

        // Return the registration template for rendering.
        return "register";
    }

    /**
     * Processes the registration information submitted by a user.
     *
     * The method checks form validation errors, validates military service
     * dates, verifies password confirmation, and attempts to register the
     * account. If registration fails, an appropriate error message is
     * added to the model and the registration page is displayed again.
     *
     * @param form submitted registration information, validated by Jakarta Validation
     * @param bindingResult contains validation errors for the submitted form
     * @param model model used to display registration errors
     * @return the registration page when validation fails, or a redirect
     *         to the login page after successful registration
     */
    @PostMapping("/register")
    public String processRegistration(
            @Valid @ModelAttribute("form") RegistrationForm form,
            BindingResult bindingResult,
            Model model) {

        // Step 1: Check field-level validation rules defined on the form.
        // Return to registration if required fields or other constraints fail.
        if (bindingResult.hasErrors()) {
            return "register";
        }

        // Step 2: Validate military service dates when both dates are provided.
        if (form.getServiceStartDate() != null
                && form.getServiceEndDate() != null) {

            // Reject a service end date that occurs before the start date.
            if (form.getServiceEndDate().isBefore(form.getServiceStartDate())) {
                model.addAttribute(
                    "registrationError",
                    "Service end date cannot be before the service start date."
                );

                // Redisplay the form with the validation message.
                return "register";
            }

            // Reject a service start date that occurs in the future.
            if (form.getServiceStartDate().isAfter(LocalDate.now())) {
                model.addAttribute(
                    "registrationError",
                    "Service start date cannot be in the future."
                );

                // Redisplay the form with the validation message.
                return "register";
            }

            // Reject a service end date that occurs in the future.
            if (form.getServiceEndDate().isAfter(LocalDate.now())) {
                model.addAttribute(
                    "registrationError",
                    "Service end date cannot be in the future."
                );

                // Redisplay the form with the validation message.
                return "register";
            }
        }

        // Step 3: Confirm that the password and confirmation password match.
        if (!form.getPassword().equals(form.getConfirmPassword())) {

            // Add an error message that the registration page can display.
            model.addAttribute("registrationError", "Passwords do not match.");

            // Redisplay the form so the user can correct the passwords.
            return "register";
        }

        try {
            // Step 4: Delegate account and veteran profile creation
            // to the registration service.
            registrationService.registerVeteran(form);

            // Redirect to the login page after successful registration.
            return "redirect:/login?registered";

        } catch (RuntimeException ex) {

            // Retrieve the exception message to determine which
            // registration error should be shown to the user.
            String message = ex.getMessage();

            // Handle duplicate usernames with a specific user-friendly message.
            if (message != null && message.contains("Username already exists")) {
                model.addAttribute(
                    "registrationError",
                    "That username is already taken. Please choose another."
                );

            // Handle password mismatch errors reported by the service.
            } else if (message != null && message.contains("Passwords do not match")) {
                model.addAttribute(
                    "registrationError",
                    "Passwords do not match."
                );

            // Display a general message for other registration failures.
            } else {
                model.addAttribute(
                    "registrationError",
                    "Registration could not be completed. Please check your information and try again."
                );
            }

            // Redisplay the registration form with the appropriate error.
            return "register";
        }
    }
}