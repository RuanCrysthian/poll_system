package com.example.poll_system.application.usecases.vote.dto;

import java.time.LocalDateTime;

public record ProcessVoteOutput(
        String id,
        String userId,
        String pollOptionId,
        String pollId,
        String status,
        LocalDateTime createdAt) {

}
