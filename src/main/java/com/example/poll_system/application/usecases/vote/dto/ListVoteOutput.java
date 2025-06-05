package com.example.poll_system.application.usecases.vote.dto;

import java.time.LocalDateTime;

public record ListVoteOutput(
        String voteId,
        String voterId,
        String pollId,
        String pollOptionId,
        LocalDateTime createdAt,
        String voteStatus) {

}
