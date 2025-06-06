package com.example.poll_system.application.usecases.vote.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final Logger logger = LoggerFactory.getLogger(SendVoteToQueue.class);

    public void execute(CreateVoteInput input) {
        validateInput(input);
        VoteCreatedEvent event = buildCreateVoteEvent(input);
        eventPublisher.publish(event);
        sendInfoLogMessageSentToQueue(event);
    }

    private void validateInput(CreateVoteInput input) {
        validateIfUserExists(input.userId());
        PollOption pollOption = validateAndGetPollOption(input.pollOptionId());
        validateIfPollIsOpen(pollOption);
    }

    private void validateIfUserExists(String userId) {
        if (!userRepository.findById(userId).isPresent()) {
            sendWarningLogMessageUserNotFound(userId);
            throw new BusinessRulesException("User not found");
        }
    }

    private void sendWarningLogMessageUserNotFound(String userId) {
        logger.warn("Vote creation failed - User not found: {}", userId);
    }

    private PollOption validateAndGetPollOption(String pollOptionId) {
        return pollOptionRepository.findById(pollOptionId)
                .orElseThrow(() -> {
                    sendWarningLogMessagePollOptionNotFound(pollOptionId);
                    return new BusinessRulesException("Poll option not found");
                });
    }

    private void sendWarningLogMessagePollOptionNotFound(String pollOptionId) {
        logger.warn("Vote creation failed - Poll option not found: {}", pollOptionId);
    }

    private void validateIfPollIsOpen(PollOption pollOption) {
        Poll poll = pollRepository.findById(pollOption.getPollId())
                .orElseThrow(() -> new BusinessRulesException("Poll not found"));

        if (!poll.isOpen()) {
            sendWarningLogMessagePollIsClosed(poll.getId());
            throw new BusinessRulesException("Poll is not open for voting");
        }
    }

    private void sendWarningLogMessagePollIsClosed(String pollId) {
        logger.warn("Vote creation failed - Poll is closed for voting: {}", pollId);
    }

    private VoteCreatedEvent buildCreateVoteEvent(CreateVoteInput input) {
        return new VoteCreatedEvent(
                input.userId(),
                input.pollOptionId());
    }

    private void sendInfoLogMessageSentToQueue(VoteCreatedEvent event) {
        logger.info("Vote sent to queue - userId: {}, pollOptionId: {}", event.getUserId(),
                event.getPollOptionId());
    }
}
