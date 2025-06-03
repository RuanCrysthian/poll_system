package com.example.poll_system.application.usecases.poll.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.example.poll_system.application.usecases.poll.dto.FindPollByIdInput;
import com.example.poll_system.application.usecases.poll.dto.FindPollByIdOutput;
import com.example.poll_system.domain.entities.Poll;
import com.example.poll_system.domain.entities.PollOption;
import com.example.poll_system.domain.exceptions.EntityNotFoundException;
import com.example.poll_system.domain.gateways.PollRepository;

public class FindPollByIdTest {

    @Mock
    private PollRepository pollRepository;

    @InjectMocks
    private FindPollById findPollById;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldThrowExceptionWhenPollNotFound() {
        String id = "aa";
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            findPollById.execute(new FindPollByIdInput(id));
        });
    }

    @Test
    void shouldReturnPollCorrectlyWhenPollExists() {
        String pollId = "poll-456";
        String title = "Scheduled Poll";
        String description = "Scheduled poll description";
        String ownerId = "owner-456";
        LocalDateTime startDate = LocalDateTime.now().plusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(3);
        List<PollOption> options = List.of(
                new PollOption("option-3", "Option A", pollId),
                new PollOption("option-4", "Option B", pollId));

        Poll poll = Poll.createScheduledPoll(pollId, title, description, ownerId, startDate, endDate, options);

        Mockito.when(pollRepository.findById(pollId)).thenReturn(Optional.of(poll));

        FindPollByIdOutput output = findPollById.execute(new FindPollByIdInput(pollId));

        Mockito.verify(pollRepository, times(1)).findById(pollId);
        Assertions.assertEquals(poll.getId(), output.pollId());
        Assertions.assertEquals(poll.getTitle(), output.title());
        Assertions.assertEquals(poll.getDescription(), output.description());
        Assertions.assertEquals(poll.getStartDate(), output.startDate());
        Assertions.assertEquals(poll.getEndDate(), output.endDate());
        Assertions.assertEquals(poll.getStatus().name(), output.status());
        assertEquals("option-3", output.pollOptions().get(0).pollOptionId());
        assertEquals("Option A", output.pollOptions().get(0).description());
        assertEquals("option-4", output.pollOptions().get(1).pollOptionId());
        assertEquals("Option B", output.pollOptions().get(1).description());
    }
}
