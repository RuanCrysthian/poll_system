package com.example.poll_system.application.usecases.vote.dto;

public record CreateVoteInput(
        String userId,
        String pollOptionId) {

}
