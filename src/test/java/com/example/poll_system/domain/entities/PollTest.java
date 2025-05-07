package com.example.poll_system.domain.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.example.poll_system.domain.enums.PollStatus;
import com.example.poll_system.domain.exceptions.BusinessRulesException;
import com.example.poll_system.domain.exceptions.FieldIsRequiredException;
import com.example.poll_system.domain.exceptions.InvalidDateTimeException;

public class PollTest {

    @Test
    void shouldCreateScheduledPoll() {
        String id = "1";
        String title = "Poll Title";
        String description = "Poll Description";
        String ownerId = "ownerId";
        LocalDateTime startDate = LocalDateTime.now().plusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(2);
        List<PollOption> options = List.of(new PollOption("1", "Option 1", id), new PollOption("2", "Option 2", id));

        Poll poll = Poll.createScheduledPoll(id, title, description, ownerId, startDate, endDate, options);

        assertEquals(PollStatus.SCHEDULED, poll.getStatus());
        assertEquals(id, poll.getId());
        assertEquals(title, poll.getTitle());
        assertEquals(description, poll.getDescription());
        assertEquals(ownerId, poll.getOwnerId());
        assertEquals(startDate, poll.getStartDate());
        assertEquals(endDate, poll.getEndDate());
        assertEquals(options, poll.getOptions());
    }

    @Test
    void shouldCreateOpenPoll() {
        String id = "1";
        String title = "Poll Title";
        String description = "Poll Description";
        String ownerId = "ownerId";
        LocalDateTime endDate = LocalDateTime.now().plusDays(2);
        List<PollOption> options = List.of(new PollOption("1", "Option 1", id), new PollOption("2", "Option 2", id));

        Poll poll = Poll.createOpenPoll(id, title, description, ownerId, endDate, options);

        assertEquals(PollStatus.OPEN, poll.getStatus());
        assertEquals(id, poll.getId());
        assertEquals(title, poll.getTitle());
        assertEquals(description, poll.getDescription());
        assertEquals(ownerId, poll.getOwnerId());
        assertEquals(endDate, poll.getEndDate());
        assertEquals(options, poll.getOptions());
    }

    @Test
    void shouldThrowExceptionWhenIdIsNull() {
        String id = null;
        String title = "Poll Title";
        String description = "Poll Description";
        String ownerId = "ownerId";
        LocalDateTime startDate = LocalDateTime.now().plusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(2);
        List<PollOption> options = List.of(new PollOption("1", "Option 1", "1"), new PollOption("2", "Option 2", "1"));

        assertThrows(FieldIsRequiredException.class, () -> {
            Poll.createScheduledPoll(id, title, description, ownerId, startDate, endDate, options);
        });
    }

    @Test
    void shouldThrowExceptionWhenIdIsEmpty() {
        String id = "  ";
        String title = "Poll Title";
        String description = "Poll Description";
        String ownerId = "ownerId";
        LocalDateTime startDate = LocalDateTime.now().plusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(2);
        List<PollOption> options = List.of(new PollOption("1", "Option 1", "1"), new PollOption("2", "Option 2", "1"));

        assertThrows(FieldIsRequiredException.class, () -> {
            Poll.createScheduledPoll(id, title, description, ownerId, startDate, endDate, options);
        });
    }

    @Test
    void shouldThrowExceptionWhenTitleIsNull() {
        String id = "1";
        String title = null;
        String description = "Poll Description";
        String ownerId = "ownerId";
        LocalDateTime startDate = LocalDateTime.now().plusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(2);
        List<PollOption> options = List.of(new PollOption("1", "Option 1", id), new PollOption("2", "Option 2", id));

        assertThrows(FieldIsRequiredException.class, () -> {
            Poll.createScheduledPoll(id, title, description, ownerId, startDate, endDate, options);
        });
    }

    @Test
    void shouldThrowExceptionWhenTitleIsEmpty() {
        String id = "1";
        String title = "  ";
        String description = "Poll Description";
        String ownerId = "ownerId";
        LocalDateTime startDate = LocalDateTime.now().plusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(2);
        List<PollOption> options = List.of(new PollOption("1", "Option 1", id), new PollOption("2", "Option 2", id));

        assertThrows(FieldIsRequiredException.class, () -> {
            Poll.createScheduledPoll(id, title, description, ownerId, startDate, endDate, options);
        });
    }

    @Test
    void shouldThrowExceptionWhenDescriptionIsNull() {
        String id = "1";
        String title = "Poll Title";
        String description = null;
        String ownerId = "ownerId";
        LocalDateTime startDate = LocalDateTime.now().plusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(2);
        List<PollOption> options = List.of(new PollOption("1", "Option 1", id), new PollOption("2", "Option 2", id));

        assertThrows(FieldIsRequiredException.class, () -> {
            Poll.createScheduledPoll(id, title, description, ownerId, startDate, endDate, options);
        });
    }

    @Test
    void shouldThrowExceptionWhenDescriptionIsEmpty() {
        String id = "1";
        String title = "Poll Title";
        String description = "  ";
        String ownerId = "ownerId";
        LocalDateTime startDate = LocalDateTime.now().plusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(2);
        List<PollOption> options = List.of(new PollOption("1", "Option 1", id), new PollOption("2", "Option 2", id));

        assertThrows(FieldIsRequiredException.class, () -> {
            Poll.createScheduledPoll(id, title, description, ownerId, startDate, endDate, options);
        });
    }

    @Test
    void shouldThrowExceptionWhenOwnerIdIsNull() {
        String id = "1";
        String title = "Poll Title";
        String description = "Poll Description";
        String ownerId = null;
        LocalDateTime startDate = LocalDateTime.now().plusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(2);
        List<PollOption> options = List.of(new PollOption("1", "Option 1", id), new PollOption("2", "Option 2", id));

        assertThrows(FieldIsRequiredException.class, () -> {
            Poll.createScheduledPoll(id, title, description, ownerId, startDate, endDate, options);
        });
    }

    @Test
    void shouldThrowExceptionWhenOwnerIdIsEmpty() {
        String id = "1";
        String title = "Poll Title";
        String description = "Poll Description";
        String ownerId = "  ";
        LocalDateTime startDate = LocalDateTime.now().plusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(2);
        List<PollOption> options = List.of(new PollOption("1", "Option 1", id), new PollOption("2", "Option 2", id));

        assertThrows(FieldIsRequiredException.class, () -> {
            Poll.createScheduledPoll(id, title, description, ownerId, startDate, endDate, options);
        });
    }

    @Test
    void shouldThrowExceptionWhenStartDateIsNull() {
        String id = "1";
        String title = "Poll Title";
        String description = "Poll Description";
        String ownerId = "ownerId";
        LocalDateTime startDate = null;
        LocalDateTime endDate = LocalDateTime.now().plusDays(2);
        List<PollOption> options = List.of(new PollOption("1", "Option 1", id), new PollOption("2", "Option 2", id));

        assertThrows(FieldIsRequiredException.class, () -> {
            Poll.createScheduledPoll(id, title, description, ownerId, startDate, endDate, options);
        });
    }

    @Test
    void shouldThrowExceptionWhenEndDateIsNull() {
        String id = "1";
        String title = "Poll Title";
        String description = "Poll Description";
        String ownerId = "ownerId";
        LocalDateTime startDate = LocalDateTime.now().plusDays(1);
        LocalDateTime endDate = null;
        List<PollOption> options = List.of(new PollOption("1", "Option 1", id), new PollOption("2", "Option 2", id));

        assertThrows(FieldIsRequiredException.class, () -> {
            Poll.createScheduledPoll(id, title, description, ownerId, startDate, endDate, options);
        });
    }

    @Test
    void shouldThrowExceptionWhenOptionsIsNull() {
        String id = "1";
        String title = "Poll Title";
        String description = "Poll Description";
        String ownerId = "ownerId";
        LocalDateTime startDate = LocalDateTime.now().plusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(2);
        List<PollOption> options = null;

        assertThrows(FieldIsRequiredException.class, () -> {
            Poll.createScheduledPoll(id, title, description, ownerId, startDate, endDate, options);
        });
    }

    @Test
    void shouldThrowExceptionWhenOptionsIsEmpty() {
        String id = "1";
        String title = "Poll Title";
        String description = "Poll Description";
        String ownerId = "ownerId";
        LocalDateTime startDate = LocalDateTime.now().plusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(2);
        List<PollOption> options = List.of();

        assertThrows(FieldIsRequiredException.class, () -> {
            Poll.createScheduledPoll(id, title, description, ownerId, startDate, endDate, options);
        });
    }

    @Test
    void shouldThrowExceptionWhenStartDateIsInPast() {
        String id = "1";
        String title = "Poll Title";
        String description = "Poll Description";
        String ownerId = "ownerId";
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(2);
        List<PollOption> options = List.of(new PollOption("1", "Option 1", id), new PollOption("2", "Option 2", id));

        assertThrows(InvalidDateTimeException.class, () -> {
            Poll.createScheduledPoll(id, title, description, ownerId, startDate, endDate, options);
        });
    }

    @Test
    void shouldThrowExceptionWhenEndDateIsBeforeStartDate() {
        String id = "1";
        String title = "Poll Title";
        String description = "Poll Description";
        String ownerId = "ownerId";
        LocalDateTime startDate = LocalDateTime.now().plusDays(2);
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);
        List<PollOption> options = List.of(new PollOption("1", "Option 1", id), new PollOption("2", "Option 2", id));

        assertThrows(InvalidDateTimeException.class, () -> {
            Poll.createScheduledPoll(id, title, description, ownerId, startDate, endDate, options);
        });
    }

    @Test
    void shouldThrowExceptionWhenOptionsSizeIsLessThanTwo() {
        String id = "1";
        String title = "Poll Title";
        String description = "Poll Description";
        String ownerId = "ownerId";
        LocalDateTime startDate = LocalDateTime.now().plusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(2);
        List<PollOption> options = List.of(new PollOption("1", "Option 1", id));

        assertThrows(BusinessRulesException.class, () -> {
            Poll.createScheduledPoll(id, title, description, ownerId, startDate, endDate, options);
        });
    }

    @Test
    void shouldThrowExceptionWhenOptionsSizeIsGreaterThanTen() {
        String id = "1";
        String title = "Poll Title";
        String description = "Poll Description";
        String ownerId = "ownerId";
        LocalDateTime startDate = LocalDateTime.now().plusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(2);
        List<PollOption> options = List.of(new PollOption("1", "Option 1", id), new PollOption("2", "Option 2", id),
                new PollOption("3", "Option 3", id), new PollOption("4", "Option 4", id),
                new PollOption("5", "Option 5", id),
                new PollOption("6", "Option 6", id), new PollOption("7", "Option 7", id),
                new PollOption("8", "Option 8", id),
                new PollOption("9", "Option 9", id), new PollOption("10", "Option 10", id),
                new PollOption("11", "Option 11", id));

        assertThrows(BusinessRulesException.class, () -> {
            Poll.createScheduledPoll(id, title, description, ownerId, startDate, endDate, options);
        });
    }

}
