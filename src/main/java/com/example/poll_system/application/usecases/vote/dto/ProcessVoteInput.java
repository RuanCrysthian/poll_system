package com.example.poll_system.application.usecases.vote.dto;

public record ProcessVoteInput(
        String userId,
        String pollOptionId) {

}
