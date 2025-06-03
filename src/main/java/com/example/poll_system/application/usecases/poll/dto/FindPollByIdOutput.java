package com.example.poll_system.application.usecases.poll.dto;

import java.time.LocalDateTime;
import java.util.List;

public record FindPollByIdOutput(
        String pollId,
        String title,
        String description,
        String ownerId,
        LocalDateTime startDate,
        LocalDateTime endDate,
        String status,
        List<PollOptionOutput> pollOptions) {

}
