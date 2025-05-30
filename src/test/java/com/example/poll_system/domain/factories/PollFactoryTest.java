package com.example.poll_system.domain.factories;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.example.poll_system.domain.entities.Poll;
import com.example.poll_system.domain.entities.PollOption;

public class PollFactoryTest {

    @Test
    void createPollFactory() {
        PollFactory factory = new PollFactory();
        Assertions.assertNotNull(factory);
    }

    @Test
    void shouldCreateScheduledPoll() {
        String id = "poll123";
        String title = "Scheduled Poll";
        String description = "This is a scheduled poll.";
        String ownerId = "owner123";
        LocalDateTime startDate = LocalDateTime.now().plusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(10);
        List<PollOption> options = List.of(new PollOption("1", "Option 1", id), new PollOption("2", "Option 2", id));

        Poll poll = PollFactory.create(id, title, description, ownerId, startDate, endDate, options);

        Assertions.assertNotNull(poll);
        Assertions.assertEquals(id, poll.getId());
        Assertions.assertEquals(title, poll.getTitle());
        Assertions.assertEquals(description, poll.getDescription());
        Assertions.assertEquals(ownerId, poll.getOwnerId());
        Assertions.assertEquals(startDate, poll.getStartDate());
        Assertions.assertEquals(endDate, poll.getEndDate());
        Assertions.assertEquals(options.size(), poll.getOptions().size());
    }

    @Test
    void shouldCreateOpenPoll() {
        String id = "poll456";
        String title = "Open Poll";
        String description = "This is an open poll.";
        String ownerId = "owner456";
        LocalDateTime endDate = LocalDateTime.now().plusDays(5);
        List<PollOption> options = List.of(new PollOption("1", "Option 1", id), new PollOption("2", "Option 2", id));
        Poll poll = PollFactory.create(id, title, description, ownerId, null, endDate, options);
        Assertions.assertNotNull(poll);
        Assertions.assertEquals(id, poll.getId());
        Assertions.assertEquals(title, poll.getTitle());
        Assertions.assertEquals(description, poll.getDescription());
        Assertions.assertEquals(ownerId, poll.getOwnerId());
        Assertions.assertNotNull(poll.getStartDate());
        Assertions.assertEquals(endDate, poll.getEndDate());
        Assertions.assertEquals(options.size(), poll.getOptions().size());
    }
}
