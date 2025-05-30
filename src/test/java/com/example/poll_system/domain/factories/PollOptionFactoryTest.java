package com.example.poll_system.domain.factories;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.example.poll_system.domain.entities.PollOption;

public class PollOptionFactoryTest {

    @Test
    void createPollOptionFactory() {
        PollOptionFactory factory = new PollOptionFactory();
        Assertions.assertNotNull(factory);
    }

    @Test
    void shouldCreatePollOption() {
        String description = "Option 1";
        String pollId = "poll123";
        PollOption pollOption = PollOptionFactory.create(description, pollId);
        Assertions.assertNotNull(pollOption);
        Assertions.assertEquals(description, pollOption.getDescription());
        Assertions.assertEquals(pollId, pollOption.getPollId());
        Assertions.assertNotNull(pollOption.getId());
    }
}
