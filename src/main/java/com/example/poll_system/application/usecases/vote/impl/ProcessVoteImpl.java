package com.example.poll_system.application.usecases.vote.impl;

import org.springframework.stereotype.Service;

import com.example.poll_system.application.usecases.vote.ProcessVote;
import com.example.poll_system.application.usecases.vote.dto.ProcessVoteInput;
import com.example.poll_system.application.usecases.vote.dto.ProcessVoteOutput;
import com.example.poll_system.domain.entities.User;
import com.example.poll_system.domain.entities.Vote;
import com.example.poll_system.domain.entities.events.VoteProcessedEvent;
import com.example.poll_system.domain.enums.VoteStatus;
import com.example.poll_system.domain.exceptions.BusinessRulesException;
import com.example.poll_system.domain.factories.VoteFactory;
import com.example.poll_system.domain.gateways.UserRepository;
import com.example.poll_system.domain.gateways.VoteRepository;
import com.example.poll_system.infrastructure.services.EventPublisher;

@Service
public class ProcessVoteImpl implements ProcessVote {

    private final VoteRepository voteRepository;
    private final UserRepository userRepository;
    private final EventPublisher eventPublisher;

    public ProcessVoteImpl(
            VoteRepository voteRepository,
            UserRepository userRepository,
            EventPublisher eventPublisher) {
        this.voteRepository = voteRepository;
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public ProcessVoteOutput execute(ProcessVoteInput input) {
        Vote vote = VoteFactory.createVote(input.userId(), input.pollOptionId());
        vote.setStatus(VoteStatus.PROCESSED);
        voteRepository.save(vote);
        User user = getValidatedUser(vote.getUserId());
        eventPublisher.publish(new VoteProcessedEvent(user.getId(), user.getEmail().getEmail(), vote.getCreatedAt()));
        return toOutput(vote);
    }

    private User getValidatedUser(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessRulesException("User not found"));
    }

    private ProcessVoteOutput toOutput(Vote vote) {
        return new ProcessVoteOutput(
                vote.getId(),
                vote.getUserId(),
                vote.getPollOptionId(),
                vote.getStatus().name(),
                vote.getCreatedAt());
    }

}
