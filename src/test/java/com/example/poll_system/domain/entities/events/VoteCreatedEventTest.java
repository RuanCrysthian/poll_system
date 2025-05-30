package com.example.poll_system.domain.entities.events;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.example.poll_system.domain.exceptions.FieldIsRequiredException;

public class VoteCreatedEventTest {

    @Test
    void shouldCreateVoteCreatedEvent() {
        String userId = "user123";
        String pollOptionId = "option456";
        LocalDateTime beforeCreation = LocalDateTime.now();

        VoteCreatedEvent event = new VoteCreatedEvent(userId, pollOptionId);

        LocalDateTime afterCreation = LocalDateTime.now();

        assertEquals(userId, event.getUserId());
        assertEquals(pollOptionId, event.getPollOptionId());
        assertEquals("VoteCreatedEvent", event.getEventName());
        assertNotNull(event.getOccurredAt());
        assertTrue(event.getOccurredAt().isAfter(beforeCreation) || event.getOccurredAt().isEqual(beforeCreation));
        assertTrue(event.getOccurredAt().isBefore(afterCreation) || event.getOccurredAt().isEqual(afterCreation));
    }

    @Test
    void shouldThrowExceptionWhenUserIdIsNull() {
        String userId = null;
        String pollOptionId = "option456";

        assertThrows(FieldIsRequiredException.class, () -> {
            new VoteCreatedEvent(userId, pollOptionId);
        });
    }

    @Test
    void shouldThrowExceptionWhenPollOptionIdIsNull() {
        String userId = "user123";
        String pollOptionId = null;

        assertThrows(FieldIsRequiredException.class, () -> {
            new VoteCreatedEvent(userId, pollOptionId);
        });
    }

    @Test
    void shouldThrowExceptionWhenUserIdIsEmpty() {
        String userId = " ";
        String pollOptionId = "option456";
        assertThrows(FieldIsRequiredException.class, () -> {
            new VoteCreatedEvent(userId, pollOptionId);
        });
    }

    @Test
    void shouldThrowExceptionWhenPollOptionIdIsEmpty() {
        String userId = "user123";
        String pollOptionId = " ";
        assertThrows(FieldIsRequiredException.class, () -> {
            new VoteCreatedEvent(userId, pollOptionId);
        });
    }

    @Test
    void shouldCreateVoteCreatedEventWithLongIds() {
        String userId = "very-long-user-id-with-many-characters-123456789";
        String pollOptionId = "very-long-poll-option-id-with-many-characters-987654321";

        VoteCreatedEvent event = new VoteCreatedEvent(userId, pollOptionId);

        assertEquals(userId, event.getUserId());
        assertEquals(pollOptionId, event.getPollOptionId());
        assertEquals("VoteCreatedEvent", event.getEventName());
        assertNotNull(event.getOccurredAt());
    }

    @Test
    void shouldCreateVoteCreatedEventWithNumericIds() {
        String userId = "12345";
        String pollOptionId = "67890";

        VoteCreatedEvent event = new VoteCreatedEvent(userId, pollOptionId);

        assertEquals(userId, event.getUserId());
        assertEquals(pollOptionId, event.getPollOptionId());
        assertEquals("VoteCreatedEvent", event.getEventName());
        assertNotNull(event.getOccurredAt());
    }

    @Test
    void shouldMaintainImmutableProperties() {
        String userId = "user123";
        String pollOptionId = "option456";

        VoteCreatedEvent event = new VoteCreatedEvent(userId, pollOptionId);

        String originalUserId = event.getUserId();
        String originalPollOptionId = event.getPollOptionId();
        String originalEventName = event.getEventName();
        LocalDateTime originalTimestamp = event.getOccurredAt();

        // Verificar que os valores não mudam após múltiplas chamadas
        assertEquals(originalUserId, event.getUserId());
        assertEquals(originalPollOptionId, event.getPollOptionId());
        assertEquals(originalEventName, event.getEventName());
        assertEquals(originalTimestamp, event.getOccurredAt());
    }

    @Test
    void shouldCreateMultipleVoteCreatedEventsWithDifferentData() {
        String userId1 = "user123";
        String pollOptionId1 = "option456";
        String userId2 = "user789";
        String pollOptionId2 = "option321";

        VoteCreatedEvent event1 = new VoteCreatedEvent(userId1, pollOptionId1);
        VoteCreatedEvent event2 = new VoteCreatedEvent(userId2, pollOptionId2);

        assertEquals(userId1, event1.getUserId());
        assertEquals(pollOptionId1, event1.getPollOptionId());
        assertEquals(userId2, event2.getUserId());
        assertEquals(pollOptionId2, event2.getPollOptionId());
        assertEquals("VoteCreatedEvent", event1.getEventName());
        assertEquals("VoteCreatedEvent", event2.getEventName());
        assertNotNull(event1.getOccurredAt());
        assertNotNull(event2.getOccurredAt());
    }

}
