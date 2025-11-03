package com.babycash.backend.exception.custom;

/**
 * Exception thrown for business logic violations
 */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
