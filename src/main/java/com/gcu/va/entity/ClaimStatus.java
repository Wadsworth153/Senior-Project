package com.gcu.va.entity;

/**
 * Defines the possible statuses of a veteran's claim.
 *
 * This enumeration provides a consistent set of values for tracking
 * a claim as it moves through the application's claim management process.
 */
public enum ClaimStatus {

    // The claim has been submitted and is awaiting further processing.
    SUBMITTED,

    // The claim is being reviewed.
    UNDER_REVIEW,

    // The claim has been approved.
    APPROVED,

    // The claim has been denied.
    DENIED
}