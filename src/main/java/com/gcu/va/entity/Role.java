package com.gcu.va.entity;

/**
 * Defines the roles available to users of the VA Benefits and Eligibility
 * Tracking System.
 *
 * These role values are used by Spring Security to control access to
 * application features and pages.
 */
public enum Role {

    // Represents an administrator who can access administrative functions.
    ROLE_ADMIN,

    // Represents a veteran who can access veteran-specific functions.
    ROLE_VETERAN
}