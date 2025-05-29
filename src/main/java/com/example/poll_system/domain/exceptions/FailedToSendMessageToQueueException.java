package com.example.poll_system.domain.exceptions;

public class FailedToSendMessageToQueueException extends RuntimeException {

    public FailedToSendMessageToQueueException(String message) {
        super(message);
    }
}
