package com.fixmycampus.model;

/** Order matters: an issue can only move forward through these states. */
public enum Status {
    REPORTED, ASSIGNED, IN_PROGRESS, RESOLVED
}
