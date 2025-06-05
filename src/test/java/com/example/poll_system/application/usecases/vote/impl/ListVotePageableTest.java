package com.example.poll_system.application.usecases.vote.impl;

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

import com.example.poll_system.application.usecases.vote.dto.ListVoteOutput;
import com.example.poll_system.domain.entities.Vote;
import com.example.poll_system.domain.enums.VoteStatus;
import com.example.poll_system.domain.factories.VoteFactory;
import com.example.poll_system.domain.gateways.VoteRepository;

public class ListVotePageableTest {

    @Mock
    private VoteRepository voteRepository;

    @InjectMocks
    private ListVotePageable listVotePageable;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnPagedVotesSuccessfully() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<Vote> votes = createVoteList();
        Page<Vote> votesPage = new PageImpl<>(votes, pageable, votes.size());

        when(voteRepository.findAll(pageable)).thenReturn(votesPage);

        // Act
        Page<ListVoteOutput> result = listVotePageable.execute(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(2, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertEquals(0, result.getNumber());

        ListVoteOutput firstVote = result.getContent().get(0);
        assertEquals("user-1", firstVote.voterId());
        assertEquals("poll-1", firstVote.pollId());
        assertEquals("option-1", firstVote.pollOptionId());
        assertEquals("UNPROCESSED", firstVote.voteStatus());

        ListVoteOutput secondVote = result.getContent().get(1);
        assertEquals("user-2", secondVote.voterId());
        assertEquals("poll-2", secondVote.pollId());
        assertEquals("option-2", secondVote.pollOptionId());
        assertEquals("PROCESSED", secondVote.voteStatus());

        verify(voteRepository, times(1)).findAll(pageable);
    }

    @Test
    void shouldReturnEmptyPageWhenNoVotesExist() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Vote> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(voteRepository.findAll(pageable)).thenReturn(emptyPage);

        // Act
        Page<ListVoteOutput> result = listVotePageable.execute(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getContent().size());
        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getTotalPages());

        verify(voteRepository, times(1)).findAll(pageable);
    }

    @Test
    void shouldReturnCorrectPageWhenUsingPagination() {
        // Arrange
        Pageable pageable = PageRequest.of(1, 1);
        List<Vote> votes = createVoteList();
        Page<Vote> votesPage = new PageImpl<>(votes.subList(1, 2), pageable, votes.size());

        when(voteRepository.findAll(pageable)).thenReturn(votesPage);

        // Act
        Page<ListVoteOutput> result = listVotePageable.execute(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getTotalPages());
        assertEquals(1, result.getNumber());

        ListVoteOutput vote = result.getContent().get(0);
        assertEquals("user-2", vote.voterId());
        assertEquals("poll-2", vote.pollId());

        verify(voteRepository, times(1)).findAll(pageable);
    }

    @Test
    void shouldMapVoteFieldsCorrectly() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        LocalDateTime createdAt = LocalDateTime.now();

        Vote vote = new Vote("vote-test", "user-test", "option-test", "poll-test",
                createdAt, VoteStatus.PROCESSED);

        Page<Vote> votesPage = new PageImpl<>(List.of(vote), pageable, 1);

        when(voteRepository.findAll(pageable)).thenReturn(votesPage);

        // Act
        Page<ListVoteOutput> result = listVotePageable.execute(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());

        ListVoteOutput voteOutput = result.getContent().get(0);
        assertEquals("vote-test", voteOutput.voteId());
        assertEquals("user-test", voteOutput.voterId());
        assertEquals("poll-test", voteOutput.pollId());
        assertEquals("option-test", voteOutput.pollOptionId());
        assertEquals(createdAt, voteOutput.createdAt());
        assertEquals("PROCESSED", voteOutput.voteStatus());

        verify(voteRepository, times(1)).findAll(pageable);
    }

    @Test
    void shouldHandleDifferentVoteStatuses() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        Vote unprocessedVote = VoteFactory.create("user-1", "option-1", "poll-1");
        Vote processedVote = VoteFactory.create("user-2", "option-2", "poll-2");
        processedVote.setStatus(VoteStatus.PROCESSED);

        List<Vote> votes = List.of(unprocessedVote, processedVote);
        Page<Vote> votesPage = new PageImpl<>(votes, pageable, votes.size());

        when(voteRepository.findAll(pageable)).thenReturn(votesPage);

        // Act
        Page<ListVoteOutput> result = listVotePageable.execute(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getContent().size());

        ListVoteOutput firstVote = result.getContent().get(0);
        assertEquals("UNPROCESSED", firstVote.voteStatus());

        ListVoteOutput secondVote = result.getContent().get(1);
        assertEquals("PROCESSED", secondVote.voteStatus());

        verify(voteRepository, times(1)).findAll(pageable);
    }

    private List<Vote> createVoteList() {
        Vote vote1 = VoteFactory.create("user-1", "option-1", "poll-1");

        Vote vote2 = VoteFactory.create("user-2", "option-2", "poll-2");
        vote2.setStatus(VoteStatus.PROCESSED);

        return List.of(vote1, vote2);
    }
}
