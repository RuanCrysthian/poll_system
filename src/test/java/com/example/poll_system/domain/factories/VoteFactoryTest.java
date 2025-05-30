package com.example.poll_system.domain.factories;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.example.poll_system.domain.entities.Vote;
import com.example.poll_system.domain.enums.VoteStatus;

public class VoteFactoryTest {

    @Test
    void createVoteFactory() {
        VoteFactory factory = new VoteFactory();
        Assertions.assertNotNull(factory);
    }

    @Test
    void shouldCreateVote() {
        String userId = "user123";
        String pollOptionId = "option456";
        Vote vote = VoteFactory.create(userId, pollOptionId);

        Assertions.assertNotNull(vote);
        Assertions.assertEquals(userId, vote.getUserId());
        Assertions.assertEquals(pollOptionId, vote.getPollOptionId());
        Assertions.assertNotNull(vote.getId());
        Assertions.assertNotNull(vote.getCreatedAt());
        Assertions.assertEquals(VoteStatus.UNPROCESSED, vote.getStatus());
    }

}
