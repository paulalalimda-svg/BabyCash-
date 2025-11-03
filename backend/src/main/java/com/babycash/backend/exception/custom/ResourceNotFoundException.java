package com.babycash.backend.exception.custom;

/**
 * Exception thrown when a resource is not found
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
