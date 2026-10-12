package com.gcu.va.controller;

// Import Spring's Controller annotation to identify this class
// as a component that handles web requests.
import org.springframework.stereotype.Controller;

// Import GetMapping to map HTTP GET requests to a controller method.
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Handles requests for the application's login page.
 *
 * This controller displays the login form when a user navigates
 * to the application's login URL.
 *
 * Authentication processing is handled by Spring Security.
 */
@Controller
public class LoginController {

    /**
     * Displays the login page.
     *
     * This method handles HTTP GET requests to the /login endpoint
     * and returns the name of the Thymeleaf template to render.
     *
     * @return the login page view name
     */
    @GetMapping("/login")
    public String loginPage() {

        // Return the login template for rendering by Spring MVC.
        return "login";
    }
}