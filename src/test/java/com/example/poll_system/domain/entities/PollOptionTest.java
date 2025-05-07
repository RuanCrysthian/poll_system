package com.example.poll_system.domain.entities;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.example.poll_system.domain.exceptions.FieldIsRequiredException;

public class PollOptionTest {

    @Test
    void shouldCreatePollOptionWithValidData() {
        String id = "1";
        String description = "Option 1";
        String pollId = "poll1";

        PollOption pollOption = new PollOption(id, description, pollId);

        assert pollOption.getId().equals(id);
        assert pollOption.getDescription().equals(description);
        assert pollOption.getPollId().equals(pollId);
    }

    @Test
    void shouldThrowExceptionWhenIdIsNull() {
        String description = "Option 1";
        String pollId = "poll1";

        Assertions.assertThrows(FieldIsRequiredException.class, () -> {
            new PollOption(null, description, pollId);
        });
    }

    @Test
    void shouldThrowExceptionWhenIdIsEmpty() {
        String description = "Option 1";
        String pollId = "poll1";

        Assertions.assertThrows(FieldIsRequiredException.class, () -> {
            new PollOption(" ", description, pollId);
        });
    }

    @Test
    void shouldThrowExceptionWhenDescriptionIsNull() {
        String id = "1";
        String pollId = "poll1";

        Assertions.assertThrows(FieldIsRequiredException.class, () -> {
            new PollOption(id, null, pollId);
        });
    }

    @Test
    void shouldThrowExceptionWhenDescriptionIsEmpty() {
        String id = "1";
        String pollId = "poll1";

        Assertions.assertThrows(FieldIsRequiredException.class, () -> {
            new PollOption(id, " ", pollId);
        });
    }

    @Test
    void shouldThrowExceptionWhenPollIdIsNull() {
        String id = "1";
        String description = "Option 1";

        Assertions.assertThrows(FieldIsRequiredException.class, () -> {
            new PollOption(id, description, null);
        });
    }

    @Test
    void shouldThrowExceptionWhenPollIdIsEmpty() {
        String id = "1";
        String description = "Option 1";

        Assertions.assertThrows(FieldIsRequiredException.class, () -> {
            new PollOption(id, description, " ");
        });
    }

}
