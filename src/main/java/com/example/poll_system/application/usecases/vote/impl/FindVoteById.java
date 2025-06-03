package com.example.poll_system.application.usecases.vote.impl;

import com.example.poll_system.application.usecases.vote.FindVoteByIdUseCase;
import com.example.poll_system.application.usecases.vote.dto.FindVoteByIdInput;
import com.example.poll_system.application.usecases.vote.dto.FindVoteByIdOutput;
import com.example.poll_system.domain.entities.Vote;
import com.example.poll_system.domain.exceptions.EntityNotFoundException;
import com.example.poll_system.domain.gateways.VoteRepository;

public class FindVoteById implements FindVoteByIdUseCase {

    private VoteRepository voteRepository;

    public FindVoteById(VoteRepository voteRepository) {
        this.voteRepository = voteRepository;
    }

    @Override
    public FindVoteByIdOutput execute(FindVoteByIdInput input) {
        Vote vote = voteRepository.findById(input.voteId())
                .orElseThrow(() -> new EntityNotFoundException("Vote not found"));
        return new FindVoteByIdOutput(
                vote.getId(),
                vote.getUserId(),
                vote.getPollId(),
                vote.getPollOptionId(),
                vote.getCreatedAt(),
                vote.getStatus().name());
    }

}
