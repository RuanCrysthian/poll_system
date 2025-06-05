package com.example.poll_system.application.usecases.vote.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.poll_system.application.usecases.vote.ListVoteUseCase;
import com.example.poll_system.application.usecases.vote.dto.ListVoteOutput;
import com.example.poll_system.domain.entities.Vote;
import com.example.poll_system.domain.gateways.VoteRepository;

public class ListVotePageable implements ListVoteUseCase {

    private final VoteRepository voteRepository;

    public ListVotePageable(VoteRepository voteRepository) {
        this.voteRepository = voteRepository;
    }

    @Override
    public Page<ListVoteOutput> execute(Pageable pageable) {
        Page<Vote> votesPage = voteRepository.findAll(pageable);
        return votesPage.map(vote -> new ListVoteOutput(
                vote.getId(),
                vote.getUserId(),
                vote.getPollId(),
                vote.getPollOptionId(),
                vote.getCreatedAt(),
                vote.getStatus().name()));
    }

}
