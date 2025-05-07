package com.example.poll_system.domain.exceptions;

public class FieldIsRequiredException extends RuntimeException {

    public FieldIsRequiredException(String message) {
        super(message);
    }
}
