package com.gcu.va.entity;

/**
 * Identifies the type of task created by a veteran.
 *
 * ACTION_PLAN represents a general task used to make progress
 * toward a personal goal.
 *
 * PREPARATION_CHECKLIST represents an item the veteran wants
 * to gather, review, or verify.
 */
public enum TaskType {

    ACTION_PLAN,
    PREPARATION_CHECKLIST
}