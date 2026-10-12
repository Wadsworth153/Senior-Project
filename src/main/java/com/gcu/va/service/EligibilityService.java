package com.gcu.va.service;

// Import LocalDate to work with service dates without time information.
import java.time.LocalDate;

// Import ChronoUnit to calculate the number of complete months between dates.
import java.time.temporal.ChronoUnit;

// Import the Spring Service annotation to register this class as a service bean.
import org.springframework.stereotype.Service;

// Import the Veteran entity containing the veteran's service information.
import com.gcu.va.entity.Veteran;

/**
 * Provides simulated eligibility calculations for veterans.
 *
 * This service evaluates recorded service dates and discharge status
 * against the application's simplified eligibility rules.
 *
 * IMPORTANT: These rules are for educational demonstration purposes only.
 * They do not represent an official VA eligibility determination.
 */
@Service
public class EligibilityService {

    /**
     * Calculates a veteran's simulated eligibility status.
     *
     * The method checks whether the veteran profile exists, whether service
     * dates are present and valid, whether dates are in the past or present,
     * whether service lasted at least 24 complete months, and whether the
     * recorded discharge status is Honorable.
     *
     * @param veteran the veteran whose service information will be evaluated
     * @return "Ineligible" when the profile or service dates are invalid,
     *         "Eligible" when the prototype requirements are met,
     *         or "Not Eligible" when the requirements are not met
     */
    public String calculateStatus(Veteran veteran) {

        // Ensure a veteran profile was provided before accessing its information.
        if (veteran == null) {
            return "Ineligible";
        }

        // Retrieve the veteran's recorded service start and end dates.
        LocalDate startDate = veteran.getServiceStartDate();
        LocalDate endDate = veteran.getServiceEndDate();

        // Required service dates must be present.
        if (startDate == null || endDate == null) {
            return "Ineligible";
        }

        // Reject service records where the end date occurs before the start date.
        if (endDate.isBefore(startDate)) {
            return "Ineligible";
        }

        // Get today's date so future service dates can be identified.
        LocalDate today = LocalDate.now();

        // Service dates cannot extend into the future.
        if (startDate.isAfter(today) || endDate.isAfter(today)) {
            return "Ineligible";
        }

        // Calculate the number of complete months between the service dates.
        long monthsOfService = ChronoUnit.MONTHS.between(
            startDate,
            endDate
        );

        // Apply the prototype's eligibility rules:
        // at least 24 complete months of service and an Honorable discharge.
        if (monthsOfService >= 24
                && "Honorable".equalsIgnoreCase(
                    veteran.getDischargeStatus())) {
            return "Eligible";
        }

        // Return this status when the valid service record does not meet
        // both the duration and discharge requirements.
        return "Not Eligible";
    }

    /**
     * Explains why a veteran meets or does not meet the prototype's
     * simulated eligibility requirements.
     *
     * The method checks the veteran profile, service dates, service duration,
     * and discharge status in sequence. It returns the first applicable
     * explanation or a success message when all requirements are met.
     *
     * @param veteran the veteran whose service information will be evaluated
     * @return an explanation of the eligibility result
     */
    public String calculateReason(Veteran veteran) {

        // Confirm that a veteran profile is available.
        if (veteran == null) {
            return "No veteran profile is available.";
        }

        // Retrieve the recorded service dates.
        LocalDate startDate = veteran.getServiceStartDate();
        LocalDate endDate = veteran.getServiceEndDate();

        // Explain the problem when either required service date is missing.
        if (startDate == null || endDate == null) {
            return "Service start date and service end date are required.";
        }

        // Explain why the service record is invalid when dates are reversed.
        if (endDate.isBefore(startDate)) {
            return "Service end date cannot be before service start date.";
        }

        // Retrieve today's date to validate the recorded service dates.
        LocalDate today = LocalDate.now();

        // Reject dates that have not yet occurred.
        if (startDate.isAfter(today) || endDate.isAfter(today)) {
            return "Service dates cannot be in the future.";
        }

        // Calculate the number of complete months of recorded service.
        long monthsOfService = ChronoUnit.MONTHS.between(
            startDate,
            endDate
        );

        // Explain when the recorded service duration is below the minimum.
        if (monthsOfService < 24) {
            return "The recorded service duration is less than 24 months.";
        }

        // Check whether the recorded discharge status meets the prototype rule.
        // equalsIgnoreCase allows the comparison to ignore letter casing.
        if (!"Honorable".equalsIgnoreCase(
                veteran.getDischargeStatus())) {
            return "The recorded discharge status does not meet the prototype's Honorable discharge requirement.";
        }

        // Return a success explanation when all prototype requirements are met.
        return "Meets the prototype's service duration and discharge requirements.";
    }
}