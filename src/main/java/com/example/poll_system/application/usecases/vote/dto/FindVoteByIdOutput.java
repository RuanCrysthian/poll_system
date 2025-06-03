package com.example.poll_system.application.usecases.vote.dto;

import java.time.LocalDateTime;

public record FindVoteByIdOutput(
        String voteId,
        String voterId,
        String pollId,
        String pollOptionId,
        LocalDateTime createdAt,
        String voteStatus) {

}
