package com.example.poll_system.application.usecases.poll.dto;

import java.time.LocalDateTime;

public record ListPollOutput(
        String pollId,
        String title,
        String description,
        String ownerId,
        LocalDateTime startDate,
        LocalDateTime endDate,
        String status) {

}
