package com.yuvaansh.fitness_tracker_api.exception;

/**
 * Thrown when a requested resource does not exist OR is not owned by the
 * authenticated user. We deliberately do not distinguish the two so we never
 * reveal the existence of another user's data (see rule.md).
 */
public class ResourceNotFoundException extends RuntimeException {
}
