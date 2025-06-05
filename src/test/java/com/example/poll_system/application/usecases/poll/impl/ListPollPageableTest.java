package com.example.poll_system.application.usecases.poll.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.example.poll_system.application.usecases.poll.dto.ListPollOutput;
import com.example.poll_system.domain.entities.Poll;
import com.example.poll_system.domain.entities.PollOption;
import com.example.poll_system.domain.factories.PollFactory;
import com.example.poll_system.domain.gateways.PollRepository;

public class ListPollPageableTest {

    @Mock
    private PollRepository pollRepository;

    @InjectMocks
    private ListPollPageable listPollPageable;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnPagedPollsSuccessfully() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<Poll> polls = createPollList();
        Page<Poll> pollsPage = new PageImpl<>(polls, pageable, polls.size());

        when(pollRepository.findAll(pageable)).thenReturn(pollsPage);

        // Act
        Page<ListPollOutput> result = listPollPageable.execute(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(2, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertEquals(0, result.getNumber());

        ListPollOutput firstPoll = result.getContent().get(0);
        assertEquals("poll-1", firstPoll.pollId());
        assertEquals("Test Poll 1", firstPoll.title());
        assertEquals("Test Description 1", firstPoll.description());
        assertEquals("owner-1", firstPoll.ownerId());
        assertEquals("OPEN", firstPoll.status());

        ListPollOutput secondPoll = result.getContent().get(1);
        assertEquals("poll-2", secondPoll.pollId());
        assertEquals("Test Poll 2", secondPoll.title());
        assertEquals("Test Description 2", secondPoll.description());
        assertEquals("owner-2", secondPoll.ownerId());
        assertEquals("SCHEDULED", secondPoll.status());

        verify(pollRepository, times(1)).findAll(pageable);
    }

    @Test
    void shouldReturnEmptyPageWhenNoPollsExist() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Poll> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(pollRepository.findAll(pageable)).thenReturn(emptyPage);

        // Act
        Page<ListPollOutput> result = listPollPageable.execute(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getContent().size());
        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getTotalPages());

        verify(pollRepository, times(1)).findAll(pageable);
    }

    @Test
    void shouldReturnCorrectPageWhenUsingPagination() {
        // Arrange
        Pageable pageable = PageRequest.of(1, 1);
        List<Poll> polls = createPollList();
        Page<Poll> pollsPage = new PageImpl<>(polls.subList(1, 2), pageable, polls.size());

        when(pollRepository.findAll(pageable)).thenReturn(pollsPage);

        // Act
        Page<ListPollOutput> result = listPollPageable.execute(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getTotalPages());
        assertEquals(1, result.getNumber());

        ListPollOutput poll = result.getContent().get(0);
        assertEquals("poll-2", poll.pollId());
        assertEquals("Test Poll 2", poll.title());

        verify(pollRepository, times(1)).findAll(pageable);
    }

    @Test
    void shouldMapPollFieldsCorrectly() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        LocalDateTime startDate = LocalDateTime.now().plusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(7);

        List<PollOption> options = List.of(
                new PollOption("option-1", "Option 1", "poll-test"),
                new PollOption("option-2", "Option 2", "poll-test"));

        Poll poll = Poll.createScheduledPoll("poll-test", "Test Poll", "Test Description",
                "owner-test", startDate, endDate, options);

        Page<Poll> pollsPage = new PageImpl<>(List.of(poll), pageable, 1);

        when(pollRepository.findAll(pageable)).thenReturn(pollsPage);

        // Act
        Page<ListPollOutput> result = listPollPageable.execute(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());

        ListPollOutput pollOutput = result.getContent().get(0);
        assertEquals("poll-test", pollOutput.pollId());
        assertEquals("Test Poll", pollOutput.title());
        assertEquals("Test Description", pollOutput.description());
        assertEquals("owner-test", pollOutput.ownerId());
        assertEquals(startDate, pollOutput.startDate());
        assertEquals(endDate, pollOutput.endDate());
        assertEquals("SCHEDULED", pollOutput.status());

        verify(pollRepository, times(1)).findAll(pageable);
    }

    private List<Poll> createPollList() {
        LocalDateTime now = LocalDateTime.now();

        List<PollOption> options1 = List.of(
                new PollOption("option-1", "Option 1", "poll-1"),
                new PollOption("option-2", "Option 2", "poll-1"));

        List<PollOption> options2 = List.of(
                new PollOption("option-3", "Option 3", "poll-2"),
                new PollOption("option-4", "Option 4", "poll-2"));

        Poll poll1 = PollFactory.create("poll-1", "Test Poll 1", "Test Description 1",
                "owner-1", null, now.plusDays(1), options1);

        Poll poll2 = Poll.createScheduledPoll("poll-2", "Test Poll 2", "Test Description 2",
                "owner-2", now.plusDays(1), now.plusDays(7), options2);

        return List.of(poll1, poll2);
    }
}
