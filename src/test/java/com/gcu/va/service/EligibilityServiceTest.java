package com.gcu.va.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.gcu.va.entity.Veteran;

/**
 * Unit tests for the EligibilityService class.
 *
 * These tests verify that the eligibility service correctly evaluates a
 * veteran's eligibility status based on service dates and discharge status.
 * They also verify how the service handles missing or invalid information
 * and whether it provides an appropriate explanation for a short service
 * duration.
 *
 * This test class uses JUnit 5 to execute the tests and assertions to compare
 * the expected results with the actual results returned by the service.
 */
class EligibilityServiceTest {

    /**
     * The EligibilityService instance used by the test methods.
     */
    private EligibilityService eligibilityService;

    /**
     * Initializes a new EligibilityService before each test.
     *
     * Creating a new instance for each test helps keep the tests independent
     * and prevents state from one test from affecting another.
     */
    @BeforeEach
    void setUp() {
        // Create the service instance that will be tested.
        eligibilityService = new EligibilityService();
    }

    /**
     * Tests that a veteran is considered eligible when their recorded service
     * duration is at least 24 months and their discharge status is Honorable.
     */
    @Test
    void eligibleWithTwentyFourMonthsAndHonorableDischarge() {

        // Create a veteran with exactly 24 months of service and an honorable discharge.
        Veteran veteran = createVeteran(
            LocalDate.of(2020, 1, 1),
            LocalDate.of(2022, 1, 1),
            "Honorable"
        );

        // Verify that the eligibility service returns the expected status.
        assertEquals(
            "Eligible",
            eligibilityService.calculateStatus(veteran)
        );
    }

    /**
     * Tests that a veteran is not eligible under the simulated rules when
     * their recorded service duration is less than 24 months, even with an
     * honorable discharge.
     */
    @Test
    void notEligibleWithLessThanTwentyFourMonths() {

        // Create a veteran with less than 24 months of service.
        Veteran veteran = createVeteran(
            LocalDate.of(2020, 1, 1),
            LocalDate.of(2021, 12, 1),
            "Honorable"
        );

        // Verify that the service returns Not Eligible.
        assertEquals(
            "Not Eligible",
            eligibilityService.calculateStatus(veteran)
        );
    }

    /**
     * Tests that a veteran with sufficient service duration but a discharge
     * status other than Honorable is considered not eligible under the
     * application's simulated eligibility rules.
     */
    @Test
    void notEligibleWithNonHonorableDischarge() {

        // Create a veteran with sufficient service duration and another discharge status.
        Veteran veteran = createVeteran(
            LocalDate.of(2020, 1, 1),
            LocalDate.of(2023, 1, 1),
            "Other"
        );

        // Verify that the service returns Not Eligible.
        assertEquals(
            "Not Eligible",
            eligibilityService.calculateStatus(veteran)
        );
    }

    /**
     * Tests that the eligibility service returns Ineligible when it receives
     * a null veteran instead of a valid Veteran object.
     */
    @Test
    void ineligibleWhenVeteranIsNull() {

        // Verify that a missing veteran record is handled as Ineligible.
        assertEquals(
            "Ineligible",
            eligibilityService.calculateStatus(null)
        );
    }

    /**
     * Tests that the eligibility service returns Ineligible when the veteran's
     * service start date and end date are both missing.
     */
    @Test
    void ineligibleWhenServiceDatesAreMissing() {

        // Create a veteran without recorded service dates.
        Veteran veteran = createVeteran(
            null,
            null,
            "Honorable"
        );

        // Verify that missing service dates produce an Ineligible result.
        assertEquals(
            "Ineligible",
            eligibilityService.calculateStatus(veteran)
        );
    }

    /**
     * Tests that the eligibility service returns Ineligible when the recorded
     * service end date occurs before the service start date.
     */
    @Test
    void ineligibleWhenEndDatePrecedesStartDate() {

        // Create a veteran with service dates in an invalid chronological order.
        Veteran veteran = createVeteran(
            LocalDate.of(2022, 1, 1),
            LocalDate.of(2020, 1, 1),
            "Honorable"
        );

        // Verify that invalid service dates produce an Ineligible result.
        assertEquals(
            "Ineligible",
            eligibilityService.calculateStatus(veteran)
        );
    }

    /**
     * Tests that the eligibility service provides a clear explanation when
     * the veteran's recorded service duration is less than 24 months.
     */
    @Test
    void eligibilityReasonExplainsShortServiceDuration() {

        // Create a veteran with 12 months of recorded service.
        Veteran veteran = createVeteran(
            LocalDate.of(2020, 1, 1),
            LocalDate.of(2021, 1, 1),
            "Honorable"
        );

        // Verify that the returned reason explains the service-duration issue.
        assertEquals(
            "The recorded service duration is less than 24 months.",
            eligibilityService.calculateReason(veteran)
        );
    }

    /**
     * Creates a Veteran object populated with the supplied service information.
     *
     * This helper method reduces duplication across the tests by allowing
     * each test to specify only the service dates and discharge status
     * relevant to its scenario.
     *
     * @param startDate the veteran's recorded service start date
     * @param endDate the veteran's recorded service end date
     * @param dischargeStatus the veteran's recorded discharge status
     * @return a Veteran object containing the supplied service information
     */
    private Veteran createVeteran(
            LocalDate startDate,
            LocalDate endDate,
            String dischargeStatus) {

        // Create a new Veteran object for the test.
        Veteran veteran = new Veteran();

        // Populate the service start date.
        veteran.setServiceStartDate(startDate);

        // Populate the service end date.
        veteran.setServiceEndDate(endDate);

        // Populate the discharge status.
        veteran.setDischargeStatus(dischargeStatus);

        // Return the completed test object.
        return veteran;
    }
}