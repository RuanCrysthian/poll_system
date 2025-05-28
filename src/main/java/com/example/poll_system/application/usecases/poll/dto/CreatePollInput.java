package com.example.poll_system.application.usecases.poll.dto;

import java.time.LocalDateTime;
import java.util.List;

public record CreatePollInput(
        String title,
        String description,
        LocalDateTime startDate,
        LocalDateTime endDate,
        String ownerId,
        List<PollOptionInput> options) {

}
