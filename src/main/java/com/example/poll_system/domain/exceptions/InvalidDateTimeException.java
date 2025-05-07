package com.example.poll_system.domain.exceptions;

public class InvalidDateTimeException extends RuntimeException {

    public InvalidDateTimeException(String message) {
        super(message);
    }
}
