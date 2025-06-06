package com.example.poll_system.application.usecases.poll.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.poll_system.application.usecases.poll.ClosePoll;
import com.example.poll_system.application.usecases.poll.dto.ClosePollInput;
import com.example.poll_system.application.usecases.poll.dto.ClosePollOutput;
import com.example.poll_system.application.usecases.poll.dto.PollOptionOutput;
import com.example.poll_system.domain.entities.Poll;
import com.example.poll_system.domain.entities.User;
import com.example.poll_system.domain.entities.events.PollClosedEvent;
import com.example.poll_system.domain.exceptions.EntityNotFoundException;
import com.example.poll_system.domain.gateways.PollRepository;
import com.example.poll_system.domain.gateways.UserRepository;
import com.example.poll_system.infrastructure.services.EventPublisher;

@Service
public class ClosePollImpl implements ClosePoll {

    private static final String POLL_NOT_FOUND_MESSAGE = "Poll not found";
    private static final String POLL_OWNER_NOT_FOUND_MESSAGE = "Poll owner not found";

    private final PollRepository pollRepository;
    private final UserRepository userRepository;
    private final EventPublisher eventPublisher;

    public ClosePollImpl(
            PollRepository pollRepository,
            UserRepository userRepository,
            EventPublisher eventPublisher) {
        this.pollRepository = pollRepository;
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
    }

    private final Logger logger = LoggerFactory.getLogger(ClosePollImpl.class);

    @Override
    public ClosePollOutput execute(ClosePollInput input) {
        Poll pollToClose = findPollById(input.pollId());
        pollToClose.close();
        pollRepository.update(pollToClose);
        User owner = findOwnerById(pollToClose.getOwnerId());
        publishPollClosedEvent(pollToClose, owner);
        sendInfoMessagePollClosed(pollToClose);
        return toOutput(pollToClose);
    }

    private Poll findPollById(String pollId) {
        return pollRepository.findById(pollId)
                .orElseThrow(() -> {
                    sendWarningLogMessagePollNotFound(pollId);
                    return new EntityNotFoundException(POLL_NOT_FOUND_MESSAGE);
                });
    }

    private User findOwnerById(String ownerId) {
        return userRepository.findById(ownerId)
                .orElseThrow(() -> {
                    sendWarningLogMessagePollOwnerNotFound(ownerId);
                    return new EntityNotFoundException(POLL_OWNER_NOT_FOUND_MESSAGE);
                });
    }

    private void sendWarningLogMessagePollNotFound(String pollId) {
        logger.warn("Poll closing failed - Poll not found: {}", pollId);
    }

    private void sendWarningLogMessagePollOwnerNotFound(String ownerId) {
        logger.warn("Poll closing failed - Poll owner not found: {}", ownerId);
    }

    private void publishPollClosedEvent(Poll poll, User owner) {
        PollClosedEvent event = createPollClosedEvent(poll, owner);
        eventPublisher.publish(event);
        sendInfoLogMessagePollClosedEvent(event);
    }

    private void sendInfoLogMessagePollClosedEvent(PollClosedEvent event) {
        logger.info("Poll closed event published - Poll ID: {}, Owner Email: {}, Title: {}",
                event.getPollId(), event.getOwnerEmail(), event.getPollTitle());
    }

    private PollClosedEvent createPollClosedEvent(Poll poll, User owner) {
        return new PollClosedEvent(
                poll.getId(),
                poll.getOwnerId(),
                owner.getEmail().getEmail(),
                poll.getTitle(),
                poll.getDescription(),
                poll.getEndDate());
    }

    private void sendInfoMessagePollClosed(Poll poll) {
        logger.info("Poll closed successfully - Poll ID: {}, Title: {}", poll.getId(), poll.getTitle());
    }

    private ClosePollOutput toOutput(Poll poll) {
        return new ClosePollOutput(
                poll.getId(),
                poll.getTitle(),
                poll.getDescription(),
                poll.getOwnerId(),
                poll.getStatus().name(),
                poll.getStartDate(),
                poll.getEndDate(),
                poll.getOptions().stream()
                        .map(option -> new PollOptionOutput(
                                option.getId(),
                                option.getDescription()))
                        .toList());
    }

}
