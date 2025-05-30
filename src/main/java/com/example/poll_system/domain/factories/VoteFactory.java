package com.example.poll_system.domain.factories;

import java.time.LocalDateTime;
import java.util.UUID;

import com.example.poll_system.domain.entities.Vote;
import com.example.poll_system.domain.enums.VoteStatus;

public class VoteFactory {

    public static Vote create(String userId, String pollOptionId, String pollId) {
        return new Vote(
                UUID.randomUUID().toString(),
                userId,
                pollOptionId,
                pollId,
                LocalDateTime.now(),
                VoteStatus.UNPROCESSED);
    }
}
