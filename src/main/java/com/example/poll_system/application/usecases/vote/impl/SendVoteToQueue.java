package com.example.poll_system.application.usecases.vote.impl;

import org.springframework.stereotype.Service;

import com.example.poll_system.application.usecases.vote.CreateVote;
import com.example.poll_system.application.usecases.vote.dto.CreateVoteInput;
import com.example.poll_system.domain.entities.Poll;
import com.example.poll_system.domain.entities.PollOption;
import com.example.poll_system.domain.entities.events.VoteCreatedEvent;
import com.example.poll_system.domain.exceptions.BusinessRulesException;
import com.example.poll_system.domain.gateways.PollOptionRepository;
import com.example.poll_system.domain.gateways.PollRepository;
import com.example.poll_system.domain.gateways.UserRepository;
import com.example.poll_system.infrastructure.services.EventPublisher;

@Service
public class SendVoteToQueue implements CreateVote {

    private final PollOptionRepository pollOptionRepository;
    private final UserRepository userRepository;
    private final PollRepository pollRepository;
    private final EventPublisher eventPublisher;

    public SendVoteToQueue(
            PollOptionRepository pollOptionRepository,
            UserRepository userRepository,
            PollRepository pollRepository,
            EventPublisher eventPublisher) {
        this.pollOptionRepository = pollOptionRepository;
        this.userRepository = userRepository;
        this.pollRepository = pollRepository;
        this.eventPublisher = eventPublisher;
    }

    public void execute(CreateVoteInput input) {
        validateInput(input);
        VoteCreatedEvent event = buildCreateVoteEvent(input);
        eventPublisher.publish(event);
    }

    private void validateInput(CreateVoteInput input) {
        validateIfUserExists(input.userId());
        PollOption pollOption = validateAndGetPollOption(input.pollOptionId());
        validateIfPollIsOpen(pollOption);
    }

    private void validateIfUserExists(String userId) {
        if (!userRepository.findById(userId).isPresent()) {
            throw new BusinessRulesException("User not found");
        }
    }

    private PollOption validateAndGetPollOption(String pollOptionId) {
        return pollOptionRepository.findById(pollOptionId)
                .orElseThrow(() -> new BusinessRulesException("Poll option not found"));
    }

    private void validateIfPollIsOpen(PollOption pollOption) {
        Poll poll = pollRepository.findById(pollOption.getPollId())
                .orElseThrow(() -> new BusinessRulesException("Poll not found"));

        if (!poll.isOpen()) {
            throw new BusinessRulesException("Poll is not open for voting");
        }
    }

    private VoteCreatedEvent buildCreateVoteEvent(CreateVoteInput input) {
        return new VoteCreatedEvent(
                input.userId(),
                input.pollOptionId());
    }
}
