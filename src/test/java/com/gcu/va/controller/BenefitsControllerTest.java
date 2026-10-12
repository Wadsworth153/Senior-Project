
package com.gcu.va.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the benefits and resources page.
 *
 * These tests verify that authenticated users can access the page,
 * that the expected resource information is rendered, and that
 * unauthenticated users cannot access the protected route.
 */
@SpringBootTest
@AutoConfigureMockMvc
class BenefitsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * Verifies that an authenticated veteran can access the benefits page.
     *
     * @throws Exception if the simulated HTTP request fails
     */
    @Test
    void benefitsPageLoadsForAuthenticatedVeteran() throws Exception {
        mockMvc.perform(get("/benefits")
                .with(user("testveteran").roles("VETERAN")))
            .andExpect(status().isOk());
    }

    /**
     * Verifies that the rendered page contains its main heading
     * and representative benefits information.
     *
     * @throws Exception if the simulated HTTP request fails
     */
    @Test
    void benefitsPageDisplaysResourceInformation() throws Exception {
        mockMvc.perform(get("/benefits")
                .with(user("testveteran").roles("VETERAN")))
            .andExpect(status().isOk())
            .andExpect(content().string(
                org.hamcrest.Matchers.containsString("Veteran Benefits")))
            .andExpect(content().string(
                org.hamcrest.Matchers.containsString("Disability Compensation")))
            .andExpect(content().string(
                org.hamcrest.Matchers.containsString("Education and Training")));
    }

    /**
     * Verifies that an unauthenticated visitor is redirected to login
     * when attempting to access the protected benefits page.
     *
     * @throws Exception if the simulated HTTP request fails
     */
    @Test
    void unauthenticatedUserCannotAccessBenefitsPage() throws Exception {
        mockMvc.perform(get("/benefits"))
            .andExpect(status().is3xxRedirection());
    }
}

