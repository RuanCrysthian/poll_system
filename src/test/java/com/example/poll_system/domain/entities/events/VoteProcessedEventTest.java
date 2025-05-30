package com.example.poll_system.domain.entities.events;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.example.poll_system.domain.exceptions.FieldIsRequiredException;

public class VoteProcessedEventTest {

    @Test
    void shouldCreateVoteProcessedEvent() {
        String userId = "user123";
        String userEmail = "user@example.com";
        LocalDateTime voteDate = LocalDateTime.of(2023, 12, 25, 10, 30, 0);
        LocalDateTime beforeCreation = LocalDateTime.now();

        VoteProcessedEvent event = new VoteProcessedEvent(userId, userEmail, voteDate);

        LocalDateTime afterCreation = LocalDateTime.now();

        assertEquals(userId, event.getUserId());
        assertEquals(userEmail, event.getUserEmail());
        assertEquals(voteDate, event.getVoteDate());
        assertEquals("VoteProcessedEvent", event.getEventName());
        assertNotNull(event.getOccurredAt());
        assertTrue(event.getOccurredAt().isAfter(beforeCreation) || event.getOccurredAt().isEqual(beforeCreation));
        assertTrue(event.getOccurredAt().isBefore(afterCreation) || event.getOccurredAt().isEqual(afterCreation));
    }

    @Test
    void shouldCreateVoteProcessedEventWithCurrentDateTime() {
        String userId = "user456";
        String userEmail = "user456@example.com";
        LocalDateTime voteDate = LocalDateTime.now();

        VoteProcessedEvent event = new VoteProcessedEvent(userId, userEmail, voteDate);

        assertEquals(userId, event.getUserId());
        assertEquals(userEmail, event.getUserEmail());
        assertEquals(voteDate, event.getVoteDate());
        assertEquals("VoteProcessedEvent", event.getEventName());
        assertNotNull(event.getOccurredAt());
    }

    @Test
    void shouldThrowExceptionWhenUserIdIsNull() {
        String userId = null;
        String userEmail = "user@example.com";
        LocalDateTime voteDate = LocalDateTime.now();

        assertThrows(FieldIsRequiredException.class, () -> {
            new VoteProcessedEvent(userId, userEmail, voteDate);
        });
    }

    @Test
    void shouldThrowExceptionWhenUserIdIsEmpty() {
        String userId = "";
        String userEmail = "user@example.com";
        LocalDateTime voteDate = LocalDateTime.now();

        assertThrows(FieldIsRequiredException.class, () -> {
            new VoteProcessedEvent(userId, userEmail, voteDate);
        });
    }

    @Test
    void shouldThrowExceptionWhenUserIdIsOnlySpaces() {
        String userId = "   ";
        String userEmail = "user@example.com";
        LocalDateTime voteDate = LocalDateTime.now();

        assertThrows(FieldIsRequiredException.class, () -> {
            new VoteProcessedEvent(userId, userEmail, voteDate);
        });
    }

    @Test
    void shouldThrowExceptionWhenUserEmailIsNull() {
        String userId = "user123";
        String userEmail = null;
        LocalDateTime voteDate = LocalDateTime.now();

        assertThrows(FieldIsRequiredException.class, () -> {
            new VoteProcessedEvent(userId, userEmail, voteDate);
        });
    }

    @Test
    void shouldThrowExceptionWhenUserEmailIsEmpty() {
        String userId = "user123";
        String userEmail = "";
        LocalDateTime voteDate = LocalDateTime.now();

        assertThrows(FieldIsRequiredException.class, () -> {
            new VoteProcessedEvent(userId, userEmail, voteDate);
        });
    }

    @Test
    void shouldThrowExceptionWhenUserEmailIsOnlySpaces() {
        String userId = "user123";
        String userEmail = "   ";
        LocalDateTime voteDate = LocalDateTime.now();

        assertThrows(FieldIsRequiredException.class, () -> {
            new VoteProcessedEvent(userId, userEmail, voteDate);
        });
    }

    @Test
    void shouldThrowExceptionWhenVoteDateIsNull() {
        String userId = "user123";
        String userEmail = "user@example.com";
        LocalDateTime voteDate = null;

        assertThrows(FieldIsRequiredException.class, () -> {
            new VoteProcessedEvent(userId, userEmail, voteDate);
        });
    }

    @Test
    void shouldCreateVoteProcessedEventWithLongStrings() {
        String userId = "very-long-user-id-with-many-characters-123456789";
        String userEmail = "very-long-email-address@very-long-domain-name.com";
        LocalDateTime voteDate = LocalDateTime.now();

        VoteProcessedEvent event = new VoteProcessedEvent(userId, userEmail, voteDate);

        assertEquals(userId, event.getUserId());
        assertEquals(userEmail, event.getUserEmail());
        assertEquals(voteDate, event.getVoteDate());
        assertEquals("VoteProcessedEvent", event.getEventName());
        assertNotNull(event.getOccurredAt());
    }

    @Test
    void shouldCreateVoteProcessedEventWithPastVoteDate() {
        String userId = "user123";
        String userEmail = "user@example.com";
        LocalDateTime voteDate = LocalDateTime.of(2020, 1, 1, 0, 0, 0);

        VoteProcessedEvent event = new VoteProcessedEvent(userId, userEmail, voteDate);

        assertEquals(userId, event.getUserId());
        assertEquals(userEmail, event.getUserEmail());
        assertEquals(voteDate, event.getVoteDate());
        assertEquals("VoteProcessedEvent", event.getEventName());
        assertNotNull(event.getOccurredAt());
        assertTrue(event.getOccurredAt().isAfter(voteDate));
    }

    @Test
    void shouldCreateVoteProcessedEventWithFutureVoteDate() {
        String userId = "user123";
        String userEmail = "user@example.com";
        LocalDateTime voteDate = LocalDateTime.of(2030, 12, 31, 23, 59, 59);

        VoteProcessedEvent event = new VoteProcessedEvent(userId, userEmail, voteDate);

        assertEquals(userId, event.getUserId());
        assertEquals(userEmail, event.getUserEmail());
        assertEquals(voteDate, event.getVoteDate());
        assertEquals("VoteProcessedEvent", event.getEventName());
        assertNotNull(event.getOccurredAt());
        assertTrue(event.getOccurredAt().isBefore(voteDate));
    }

    @Test
    void shouldMaintainImmutableProperties() {
        String userId = "user123";
        String userEmail = "user@example.com";
        LocalDateTime voteDate = LocalDateTime.of(2023, 12, 25, 10, 30, 0);

        VoteProcessedEvent event = new VoteProcessedEvent(userId, userEmail, voteDate);

        String originalUserId = event.getUserId();
        String originalUserEmail = event.getUserEmail();
        LocalDateTime originalVoteDate = event.getVoteDate();
        String originalEventName = event.getEventName();
        LocalDateTime originalTimestamp = event.getOccurredAt();

        // Verificar que os valores não mudam após múltiplas chamadas
        assertEquals(originalUserId, event.getUserId());
        assertEquals(originalUserEmail, event.getUserEmail());
        assertEquals(originalVoteDate, event.getVoteDate());
        assertEquals(originalEventName, event.getEventName());
        assertEquals(originalTimestamp, event.getOccurredAt());
    }

    @Test
    void shouldCreateMultipleVoteProcessedEventsWithDifferentData() {
        String userId1 = "user123";
        String userEmail1 = "user1@example.com";
        LocalDateTime voteDate1 = LocalDateTime.of(2023, 12, 25, 10, 30, 0);

        String userId2 = "user456";
        String userEmail2 = "user2@example.com";
        LocalDateTime voteDate2 = LocalDateTime.of(2023, 12, 26, 11, 45, 0);

        VoteProcessedEvent event1 = new VoteProcessedEvent(userId1, userEmail1, voteDate1);
        VoteProcessedEvent event2 = new VoteProcessedEvent(userId2, userEmail2, voteDate2);

        assertEquals(userId1, event1.getUserId());
        assertEquals(userEmail1, event1.getUserEmail());
        assertEquals(voteDate1, event1.getVoteDate());
        assertEquals(userId2, event2.getUserId());
        assertEquals(userEmail2, event2.getUserEmail());
        assertEquals(voteDate2, event2.getVoteDate());
        assertEquals("VoteProcessedEvent", event1.getEventName());
        assertEquals("VoteProcessedEvent", event2.getEventName());
        assertNotNull(event1.getOccurredAt());
        assertNotNull(event2.getOccurredAt());
    }

    @Test
    void shouldCreateVoteProcessedEventWithSpecificDateTimeValues() {
        String userId = "user123";
        String userEmail = "user@example.com";
        LocalDateTime voteDate = LocalDateTime.of(2023, 6, 15, 14, 30, 45);

        VoteProcessedEvent event = new VoteProcessedEvent(userId, userEmail, voteDate);

        assertEquals(voteDate, event.getVoteDate());
        assertEquals(2023, event.getVoteDate().getYear());
        assertEquals(6, event.getVoteDate().getMonthValue());
        assertEquals(15, event.getVoteDate().getDayOfMonth());
        assertEquals(14, event.getVoteDate().getHour());
        assertEquals(30, event.getVoteDate().getMinute());
        assertEquals(45, event.getVoteDate().getSecond());
    }

    @Test
    void shouldCreateVoteProcessedEventWithNumericIds() {
        String userId = "12345";
        String userEmail = "numeric@example.com";
        LocalDateTime voteDate = LocalDateTime.now();

        VoteProcessedEvent event = new VoteProcessedEvent(userId, userEmail, voteDate);

        assertEquals(userId, event.getUserId());
        assertEquals(userEmail, event.getUserEmail());
        assertEquals(voteDate, event.getVoteDate());
        assertEquals("VoteProcessedEvent", event.getEventName());
        assertNotNull(event.getOccurredAt());
    }

}
