package com.example.poll_system.application.usecases.vote.impl;

import static org.mockito.Mockito.times;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.example.poll_system.application.usecases.vote.dto.FindVoteByIdInput;
import com.example.poll_system.application.usecases.vote.dto.FindVoteByIdOutput;
import com.example.poll_system.domain.entities.Vote;
import com.example.poll_system.domain.exceptions.EntityNotFoundException;
import com.example.poll_system.domain.factories.VoteFactory;
import com.example.poll_system.domain.gateways.VoteRepository;

public class FindVoteByIdTest {

    @Mock
    private VoteRepository voteRepository;

    @InjectMocks
    private FindVoteById findVoteById;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldThrowExceptionWhenVoteNotFound() {
        String voteId = "a";
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            findVoteById.execute(new FindVoteByIdInput(voteId));
        });
    }

    @Test
    void shouldReturnVoteCorrectlyWhenVoteExists() {
        Vote vote = VoteFactory.create("123", "qaz", "qwe");
        Mockito.when(voteRepository.findById(vote.getId())).thenReturn(Optional.of(vote));

        FindVoteByIdInput input = new FindVoteByIdInput(vote.getId());

        FindVoteByIdOutput output = findVoteById.execute(input);

        Assertions.assertEquals(vote.getId(), output.voteId());
        Assertions.assertEquals(vote.getUserId(), output.voterId());
        Assertions.assertEquals(vote.getPollId(), output.pollId());
        Assertions.assertEquals(vote.getPollOptionId(), output.pollOptionId());
        Assertions.assertEquals(vote.getCreatedAt(), output.createdAt());
        Assertions.assertEquals(vote.getStatus().name(), output.voteStatus());
        Mockito.verify(voteRepository, times(1)).findById(vote.getId());
    }
}
