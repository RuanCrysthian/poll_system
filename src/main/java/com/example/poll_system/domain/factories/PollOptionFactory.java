package com.example.poll_system.domain.factories;

import java.util.UUID;

import com.example.poll_system.domain.entities.PollOption;

public class PollOptionFactory {

    public static PollOption create(String description, String pollId) {
        return new PollOption(
                UUID.randomUUID().toString(),
                description,
                pollId);
    }
}
