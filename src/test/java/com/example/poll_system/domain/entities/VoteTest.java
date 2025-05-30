package com.example.poll_system.domain.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.example.poll_system.domain.enums.VoteStatus;
import com.example.poll_system.domain.exceptions.FieldIsRequiredException;

public class VoteTest {

    @Test
    void shouldCreateVoteWithUnprocessedStatus() {
        String id = "1";
        String userId = "user123";
        String pollOptionId = "option456";
        String pollId = "poll789";
        LocalDateTime createdAt = LocalDateTime.now();
        VoteStatus status = VoteStatus.UNPROCESSED;

        Vote vote = new Vote(id, userId, pollOptionId, pollId, createdAt, status);

        assertEquals(id, vote.getId());
        assertEquals(userId, vote.getUserId());
        assertEquals(pollOptionId, vote.getPollOptionId());
        assertEquals(pollId, vote.getPollId());
        assertEquals(createdAt, vote.getCreatedAt());
        assertEquals(status, vote.getStatus());
    }

    @Test
    void shouldCreateVoteWithProcessedStatus() {
        String id = "2";
        String userId = "user789";
        String pollOptionId = "option123";
        String pollId = "poll456";
        LocalDateTime createdAt = LocalDateTime.now();
        VoteStatus status = VoteStatus.PROCESSED;

        Vote vote = new Vote(id, userId, pollOptionId, pollId, createdAt, status);

        assertEquals(id, vote.getId());
        assertEquals(userId, vote.getUserId());
        assertEquals(pollOptionId, vote.getPollOptionId());
        assertEquals(pollId, vote.getPollId());
        assertEquals(createdAt, vote.getCreatedAt());
        assertEquals(status, vote.getStatus());
    }

    @Test
    void shouldThrowExceptionWhenIdIsNull() {
        String id = null;
        String userId = "user123";
        String pollOptionId = "option456";
        String pollId = "poll789";
        LocalDateTime createdAt = LocalDateTime.now();
        VoteStatus status = VoteStatus.UNPROCESSED;

        assertThrows(FieldIsRequiredException.class, () -> {
            new Vote(id, userId, pollOptionId, pollId, createdAt, status);
        });
    }

    @Test
    void shouldThrowExceptionWhenIdIsEmpty() {
        String id = "";
        String userId = "user123";
        String pollOptionId = "option456";
        String pollId = "poll789";
        LocalDateTime createdAt = LocalDateTime.now();
        VoteStatus status = VoteStatus.UNPROCESSED;

        assertThrows(FieldIsRequiredException.class, () -> {
            new Vote(id, userId, pollOptionId, pollId, createdAt, status);
        });
    }

    @Test
    void shouldThrowExceptionWhenUserIdIsNull() {
        String id = "1";
        String userId = null;
        String pollOptionId = "option456";
        String pollId = "poll789";
        LocalDateTime createdAt = LocalDateTime.now();
        VoteStatus status = VoteStatus.UNPROCESSED;

        assertThrows(FieldIsRequiredException.class, () -> {
            new Vote(id, userId, pollOptionId, pollId, createdAt, status);
        });
    }

    @Test
    void shouldThrowExceptionWhenUserIdIsEmpty() {
        String id = "1";
        String userId = "";
        String pollOptionId = "option456";
        String pollId = "poll789";
        LocalDateTime createdAt = LocalDateTime.now();
        VoteStatus status = VoteStatus.UNPROCESSED;

        assertThrows(FieldIsRequiredException.class, () -> {
            new Vote(id, userId, pollOptionId, pollId, createdAt, status);
        });
    }

    @Test
    void shouldThrowExceptionWhenPollOptionIdIsNull() {
        String id = "1";
        String userId = "user123";
        String pollOptionId = null;
        String pollId = "poll789";
        LocalDateTime createdAt = LocalDateTime.now();
        VoteStatus status = VoteStatus.UNPROCESSED;

        assertThrows(FieldIsRequiredException.class, () -> {
            new Vote(id, userId, pollOptionId, pollId, createdAt, status);
        });
    }

    @Test
    void shouldThrowExceptionWhenPollOptionIdIsEmpty() {
        String id = "1";
        String userId = "user123";
        String pollOptionId = "";
        String pollId = "poll789";
        LocalDateTime createdAt = LocalDateTime.now();
        VoteStatus status = VoteStatus.UNPROCESSED;

        assertThrows(FieldIsRequiredException.class, () -> {
            new Vote(id, userId, pollOptionId, pollId, createdAt, status);
        });
    }

    @Test
    void shouldThrowExceptionWhenCreatedAtIsNull() {
        String id = "1";
        String userId = "user123";
        String pollOptionId = "option456";
        String pollId = "poll789";
        LocalDateTime createdAt = null;
        VoteStatus status = VoteStatus.UNPROCESSED;

        assertThrows(FieldIsRequiredException.class, () -> {
            new Vote(id, userId, pollOptionId, pollId, createdAt, status);
        });
    }

    @Test
    void shouldThrowExceptionWhenStatusIsNull() {
        String id = "1";
        String userId = "user123";
        String pollOptionId = "option456";
        String pollId = "poll789";
        LocalDateTime createdAt = LocalDateTime.now();
        VoteStatus status = null;

        assertThrows(FieldIsRequiredException.class, () -> {
            new Vote(id, userId, pollOptionId, pollId, createdAt, status);
        });
    }

    @Test
    void shouldChangeStatusFromUnprocessedToProcessed() {
        String id = "1";
        String userId = "user123";
        String pollOptionId = "option456";
        String pollId = "poll789";
        LocalDateTime createdAt = LocalDateTime.now();
        VoteStatus status = VoteStatus.UNPROCESSED;

        Vote vote = new Vote(id, userId, pollOptionId, pollId, createdAt, status);
        assertEquals(VoteStatus.UNPROCESSED, vote.getStatus());

        vote.setStatus(VoteStatus.PROCESSED);
        assertEquals(VoteStatus.PROCESSED, vote.getStatus());
    }

    @Test
    void shouldChangeStatusFromProcessedToUnprocessed() {
        String id = "1";
        String userId = "user123";
        String pollOptionId = "option456";
        String pollId = "poll789";
        LocalDateTime createdAt = LocalDateTime.now();
        VoteStatus status = VoteStatus.PROCESSED;

        Vote vote = new Vote(id, userId, pollOptionId, pollId, createdAt, status);
        assertEquals(VoteStatus.PROCESSED, vote.getStatus());

        vote.setStatus(VoteStatus.UNPROCESSED);
        assertEquals(VoteStatus.UNPROCESSED, vote.getStatus());
    }

    @Test
    void shouldThrowExceptionWhenIdIsOnlySpaces() {
        String id = "   ";
        String userId = "user123";
        String pollOptionId = "option456";
        String pollId = "poll789";
        LocalDateTime createdAt = LocalDateTime.now();
        VoteStatus status = VoteStatus.UNPROCESSED;

        assertThrows(FieldIsRequiredException.class, () -> {
            new Vote(id, userId, pollOptionId, pollId, createdAt, status);
        });
    }

    @Test
    void shouldThrowExceptionWhenUserIdIsOnlySpaces() {
        String id = "1";
        String userId = "   ";
        String pollOptionId = "option456";
        String pollId = "poll789";
        LocalDateTime createdAt = LocalDateTime.now();
        VoteStatus status = VoteStatus.UNPROCESSED;

        assertThrows(FieldIsRequiredException.class, () -> {
            new Vote(id, userId, pollOptionId, pollId, createdAt, status);
        });
    }

    @Test
    void shouldThrowExceptionWhenPollOptionIdIsOnlySpaces() {
        String id = "1";
        String userId = "user123";
        String pollOptionId = "   ";
        String pollId = "poll789";
        LocalDateTime createdAt = LocalDateTime.now();
        VoteStatus status = VoteStatus.UNPROCESSED;

        assertThrows(FieldIsRequiredException.class, () -> {
            new Vote(id, userId, pollOptionId, pollId, createdAt, status);
        });
    }

    @Test
    void shouldThrowExceptionWhenPollIdIsNull() {
        String id = "1";
        String userId = "user123";
        String pollOptionId = "option456";
        String pollId = null;
        LocalDateTime createdAt = LocalDateTime.now();
        VoteStatus status = VoteStatus.UNPROCESSED;

        assertThrows(FieldIsRequiredException.class, () -> {
            new Vote(id, userId, pollOptionId, pollId, createdAt, status);
        });
    }

    @Test
    void shouldThrowExceptionWhenPollIdIsEmpty() {
        String id = "1";
        String userId = "user123";
        String pollOptionId = "option456";
        String pollId = "";
        LocalDateTime createdAt = LocalDateTime.now();
        VoteStatus status = VoteStatus.UNPROCESSED;

        assertThrows(FieldIsRequiredException.class, () -> {
            new Vote(id, userId, pollOptionId, pollId, createdAt, status);
        });
    }

    @Test
    void shouldThrowExceptionWhenPollIdIsOnlySpaces() {
        String id = "1";
        String userId = "user123";
        String pollOptionId = "option456";
        String pollId = "   ";
        LocalDateTime createdAt = LocalDateTime.now();
        VoteStatus status = VoteStatus.UNPROCESSED;

        assertThrows(FieldIsRequiredException.class, () -> {
            new Vote(id, userId, pollOptionId, pollId, createdAt, status);
        });
    }

    @Test
    void shouldCreateVoteWithSpecificDateTime() {
        String id = "1";
        String userId = "user123";
        String pollOptionId = "option456";
        String pollId = "poll789";
        LocalDateTime createdAt = LocalDateTime.of(2023, 12, 25, 10, 30, 0);
        VoteStatus status = VoteStatus.UNPROCESSED;

        Vote vote = new Vote(id, userId, pollOptionId, pollId, createdAt, status);

        assertEquals(createdAt, vote.getCreatedAt());
        assertEquals(2023, vote.getCreatedAt().getYear());
        assertEquals(12, vote.getCreatedAt().getMonthValue());
        assertEquals(25, vote.getCreatedAt().getDayOfMonth());
        assertEquals(10, vote.getCreatedAt().getHour());
        assertEquals(30, vote.getCreatedAt().getMinute());
        assertEquals(0, vote.getCreatedAt().getSecond());
    }

    @Test
    void shouldCreateVoteWithLongIds() {
        String id = "very-long-vote-id-123456789";
        String userId = "very-long-user-id-987654321";
        String pollOptionId = "very-long-poll-option-id-456789123";
        String pollId = "very-long-poll-id-123456789";
        LocalDateTime createdAt = LocalDateTime.now();
        VoteStatus status = VoteStatus.UNPROCESSED;

        Vote vote = new Vote(id, userId, pollOptionId, pollId, createdAt, status);

        assertEquals(id, vote.getId());
        assertEquals(userId, vote.getUserId());
        assertEquals(pollOptionId, vote.getPollOptionId());
        assertEquals(pollId, vote.getPollId());
        assertEquals(createdAt, vote.getCreatedAt());
        assertEquals(status, vote.getStatus());
    }

    @Test
    void shouldCreateVoteWithNumericIds() {
        String id = "12345";
        String userId = "67890";
        String pollOptionId = "54321";
        String pollId = "98765";
        LocalDateTime createdAt = LocalDateTime.now();
        VoteStatus status = VoteStatus.PROCESSED;

        Vote vote = new Vote(id, userId, pollOptionId, pollId, createdAt, status);

        assertEquals(id, vote.getId());
        assertEquals(userId, vote.getUserId());
        assertEquals(pollOptionId, vote.getPollOptionId());
        assertEquals(pollId, vote.getPollId());
        assertEquals(status, vote.getStatus());
    }

    @Test
    void shouldMaintainStateAfterMultipleStatusChanges() {
        String id = "1";
        String userId = "user123";
        String pollOptionId = "option456";
        String pollId = "poll789";
        LocalDateTime createdAt = LocalDateTime.now();
        VoteStatus initialStatus = VoteStatus.UNPROCESSED;

        Vote vote = new Vote(id, userId, pollOptionId, pollId, createdAt, initialStatus);

        // Initial state
        assertEquals(VoteStatus.UNPROCESSED, vote.getStatus());

        // Change to PROCESSED
        vote.setStatus(VoteStatus.PROCESSED);
        assertEquals(VoteStatus.PROCESSED, vote.getStatus());

        // Change back to UNPROCESSED
        vote.setStatus(VoteStatus.UNPROCESSED);
        assertEquals(VoteStatus.UNPROCESSED, vote.getStatus());

        // Change to PROCESSED again
        vote.setStatus(VoteStatus.PROCESSED);
        assertEquals(VoteStatus.PROCESSED, vote.getStatus());

        // Verify other properties remain unchanged
        assertEquals(id, vote.getId());
        assertEquals(userId, vote.getUserId());
        assertEquals(pollOptionId, vote.getPollOptionId());
        assertEquals(pollId, vote.getPollId());
        assertEquals(createdAt, vote.getCreatedAt());
    }

}
