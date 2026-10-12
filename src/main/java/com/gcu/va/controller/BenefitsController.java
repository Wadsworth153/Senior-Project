package com.gcu.va.controller;

// Import the annotation used to identify this class as a Spring MVC controller.
import org.springframework.stereotype.Controller;

// Import the annotation used to map HTTP GET requests to a controller method.
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Handles requests for the veteran benefits and resources information page.
 *
 * This controller provides access to educational information about benefits
 * and support resources. It does not determine official VA benefit eligibility.
 */
@Controller
public class BenefitsController {

    /**
     * Displays the benefits and resources page.
     *
     * This method handles HTTP GET requests to the /benefits route.
     * Spring uses the returned view name to locate and render the corresponding
     * Thymeleaf template.
     *
     * @return the Thymeleaf template used to display benefits information
     */
    @GetMapping("/benefits")
    public String showBenefits() {
        // Return the benefits template so the user can view benefits and resources.
        return "benefits";
    }
}