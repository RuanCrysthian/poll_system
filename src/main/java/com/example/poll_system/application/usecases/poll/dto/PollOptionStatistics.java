package com.example.poll_system.application.usecases.poll.dto;

public record PollOptionStatistics(
        String pollOptionId,
        String pollOptionDescription,
        Long votesCount) {

}
