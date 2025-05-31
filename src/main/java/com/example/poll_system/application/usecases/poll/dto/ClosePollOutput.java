package com.example.poll_system.application.usecases.poll.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ClosePollOutput(
        String pollId,
        String title,
        String description,
        String ownerId,
        String status,
        LocalDateTime startDate,
        LocalDateTime endDate,
        List<PollOptionOutput> options) {

}
