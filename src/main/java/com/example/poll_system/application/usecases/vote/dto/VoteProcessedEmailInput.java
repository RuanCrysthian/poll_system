package com.example.poll_system.application.usecases.vote.dto;

public record VoteProcessedEmailInput(
        String email,
        String subject,
        String body) {

}
