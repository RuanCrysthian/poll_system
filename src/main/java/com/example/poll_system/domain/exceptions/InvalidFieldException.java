package com.example.poll_system.domain.exceptions;

public class InvalidFieldException extends RuntimeException {

    public InvalidFieldException(String message) {
        super(message);
    }
}
