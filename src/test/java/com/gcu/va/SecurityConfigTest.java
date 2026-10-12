package com.gcu.va;

// Import MockMvc request builders for simulating HTTP GET and POST requests.
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

// Import MockMvc result matchers for verifying HTTP response statuses.
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Import the matcher used to verify redirect destinations.
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

// Import JUnit's annotation for identifying test methods.
import org.junit.jupiter.api.Test;

// Import Spring's dependency injection annotation.
import org.springframework.beans.factory.annotation.Autowired;

// Import annotations for loading the Spring Boot application and configuring MockMvc.
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

// Import the annotation used to simulate an authenticated user with specific roles.
import org.springframework.security.test.context.support.WithMockUser;

// Import MockMvc for simulating HTTP requests without starting a real browser.
import org.springframework.test.web.servlet.MockMvc;

/**
 * Tests the security configuration of the VA Benefits and Eligibility
 * Tracking System.
 *
 * These tests verify authentication requirements, public page access,
 * role-based authorization, and CSRF protection.
 *
 * Spring Boot loads the application context, while MockMvc simulates
 * HTTP requests and checks the resulting responses.
 */
@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    // MockMvc instance used to simulate HTTP requests against the application.
    @Autowired
    private MockMvc mockMvc;

    /**
     * Verifies that an unauthenticated user is redirected to the login page
     * when attempting to access the protected home page.
     *
     * @throws Exception if the simulated HTTP request fails
     */
    @Test
    void unauthenticatedUserIsRedirectedToLogin() throws Exception {

        // Request the home page without providing an authenticated user.
        mockMvc.perform(get("/home"))

            // Verify that the response is a redirect.
            .andExpect(status().is3xxRedirection())

            // Verify that the redirect destination matches the login page.
            .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers
                .redirectedUrlPattern("**/login"));
    }

    /**
     * Verifies that users can access the registration page without logging in.
     *
     * @throws Exception if the simulated HTTP request fails
     */
    @Test
    void registrationPageIsPubliclyAccessible() throws Exception {

        // Request the registration page without authentication.
        mockMvc.perform(get("/register"))

            // Verify that the registration page responds successfully.
            .andExpect(status().isOk());
    }

    /**
     * Verifies that users can access the login page without authentication.
     *
     * @throws Exception if the simulated HTTP request fails
     */
    @Test
    void loginPageIsPubliclyAccessible() throws Exception {

        // Request the login page without authentication.
        mockMvc.perform(get("/login"))

            // Verify that the login page responds successfully.
            .andExpect(status().isOk());
    }

    /**
     * Verifies that a veteran cannot access an administrator-only page.
     *
     * The mock user is authenticated with the VETERAN role, but does not
     * have the ADMIN role required to access the requested page.
     *
     * @throws Exception if the simulated HTTP request fails
     */
    @Test
    @WithMockUser(username = "testveteran", roles = "VETERAN")
    void veteranCannotAccessAdminPage() throws Exception {

        // Attempt to access the administrator page as a veteran.
        mockMvc.perform(get("/admin"))

            // Verify that access is forbidden.
            .andExpect(status().isForbidden());
    }

    /**
     * Verifies that an administrator cannot access the veteran-only profile page.
     *
     * The mock user is authenticated with the ADMIN role, but does not
     * have the VETERAN role required to access the requested page.
     *
     * @throws Exception if the simulated HTTP request fails
     */
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void administratorCannotAccessVeteranProfilePage() throws Exception {

        // Attempt to access the veteran profile page as an administrator.
        mockMvc.perform(get("/my-profile"))

            // Verify that access is forbidden.
            .andExpect(status().isForbidden());
    }

    /**
     * Verifies that a POST request without a CSRF token is rejected.
     *
     * Spring Security's CSRF protection requires a valid token for
     * applicable state-changing requests.
     *
     * @throws Exception if the simulated HTTP request fails
     */
    @Test
    void postWithoutCsrfTokenIsRejected() throws Exception {

        // Submit a POST request without including a CSRF token.
        mockMvc.perform(post("/register"))

            // Verify that Spring Security rejects the request.
            .andExpect(status().isForbidden());
    }
}