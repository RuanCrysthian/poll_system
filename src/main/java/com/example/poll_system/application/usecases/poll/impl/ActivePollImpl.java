package com.example.poll_system.application.usecases.poll.impl;

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

    @Override
    public ActivePollOutput execute(ActivePollInput input) {
        Poll pollToActivate = pollRepository.findById(input.pollId())
                .orElseThrow(() -> new EntityNotFoundException("Poll not found"));
        pollToActivate.open();
        pollRepository.update(pollToActivate);
        return toOutput(pollToActivate);
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
