package com.babycash.backend.exception.custom;

/**
 * Exception thrown when authentication fails
 */
public class AuthenticationException extends RuntimeException {
    public AuthenticationException(String message) {
        super(message);
    }
}
