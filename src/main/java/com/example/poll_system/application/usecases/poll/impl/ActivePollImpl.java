package com.example.poll_system.application.usecases.poll.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.poll_system.application.usecases.poll.ActivePoll;
import com.example.poll_system.application.usecases.poll.dto.ActivePollInput;
import com.example.poll_system.application.usecases.poll.dto.ActivePollOutput;
import com.example.poll_system.application.usecases.poll.dto.PollOptionOutput;
import com.example.poll_system.domain.entities.Poll;
import com.example.poll_system.domain.exceptions.EntityNotFoundException;
import com.example.poll_system.domain.gateways.PollRepository;

@Service
public class ActivePollImpl implements ActivePoll {

    private final PollRepository pollRepository;

    public ActivePollImpl(PollRepository pollRepository) {
        this.pollRepository = pollRepository;
    }

    private final Logger logger = LoggerFactory.getLogger(ActivePollImpl.class);

    @Override
    public ActivePollOutput execute(ActivePollInput input) {
        Poll pollToActivate = pollRepository.findById(input.pollId())
                .orElseThrow(() -> {
                    sendWarningLogMessagePollNotFound(input.pollId());
                    return new EntityNotFoundException("Poll not found");
                });
        pollToActivate.open();
        pollRepository.update(pollToActivate);
        sendInfoLogMessagePollActivated(pollToActivate);
        return toOutput(pollToActivate);
    }

    private void sendWarningLogMessagePollNotFound(String pollId) {
        logger.warn("Poll activation failed - Poll not found: {}", pollId);
    }

    private void sendInfoLogMessagePollActivated(Poll poll) {
        logger.info("Poll activated successfully - Poll ID: {}, Title: {}", poll.getId(), poll.getTitle());
    }

    private ActivePollOutput toOutput(Poll poll) {
        return new ActivePollOutput(
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
