package com.example.poll_system.application.usecases.vote.impl;

import org.springframework.stereotype.Service;

import com.example.poll_system.application.usecases.vote.CreateVote;
import com.example.poll_system.application.usecases.vote.dto.CreateVoteInput;
import com.example.poll_system.domain.entities.events.VoteCreatedEvent;
import com.example.poll_system.domain.exceptions.BusinessRulesException;
import com.example.poll_system.domain.gateways.PollOptionRepository;
import com.example.poll_system.domain.gateways.UserRepository;
import com.example.poll_system.infrastructure.services.EventPublisher;

@Service
public class SendVoteToQueue implements CreateVote {

    private final PollOptionRepository pollOptionRepository;
    private final UserRepository userRepository;
    private final EventPublisher eventPublisher;

    public SendVoteToQueue(
            PollOptionRepository pollOptionRepository,
            UserRepository userRepository,
            EventPublisher eventPublisher) {
        this.pollOptionRepository = pollOptionRepository;
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
    }

    public void execute(CreateVoteInput input) {
        validateInput(input);
        VoteCreatedEvent event = buildCreateVoteEvent(input);
        eventPublisher.publish(event);
    }

    private void validateInput(CreateVoteInput input) {
        validateIfUserExists(input.userId());
        validateIfPollOptionExists(input.pollOptionId());
    }

    private void validateIfUserExists(String userId) {
        if (!userRepository.findById(userId).isPresent()) {
            throw new BusinessRulesException("User not found");
        }
    }

    private void validateIfPollOptionExists(String pollOptionId) {
        if (!pollOptionRepository.findById(pollOptionId).isPresent()) {
            throw new BusinessRulesException("Poll option not found");
        }
    }

    private VoteCreatedEvent buildCreateVoteEvent(CreateVoteInput input) {
        return new VoteCreatedEvent(
                input.userId(),
                input.pollOptionId());
    }
}
