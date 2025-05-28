package com.example.poll_system.domain.factories;

import java.time.LocalDateTime;
import java.util.List;

import com.example.poll_system.domain.entities.Poll;
import com.example.poll_system.domain.entities.PollOption;

public class PollFactory {

    public static Poll create(
            String id,
            String title,
            String description,
            String ownerId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            List<PollOption> options) {
        if (startDate != null) {
            return Poll.createScheduledPoll(
                    id,
                    title,
                    description,
                    ownerId,
                    startDate,
                    endDate,
                    options);
        }
        return Poll.createOpenPoll(
                id,
                title,
                description,
                ownerId,
                endDate,
                options);
    }
}
