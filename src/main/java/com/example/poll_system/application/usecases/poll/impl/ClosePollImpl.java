package com.example.poll_system.application.usecases.poll.impl;

import org.springframework.stereotype.Service;

import com.example.poll_system.application.usecases.poll.ClosePoll;
import com.example.poll_system.application.usecases.poll.dto.ClosePollInput;
import com.example.poll_system.application.usecases.poll.dto.ClosePollOutput;
import com.example.poll_system.application.usecases.poll.dto.PollOptionOutput;
import com.example.poll_system.domain.entities.Poll;
import com.example.poll_system.domain.exceptions.EntityNotFoundException;
import com.example.poll_system.domain.gateways.PollRepository;

@Service
public class ClosePollImpl implements ClosePoll {

    private final PollRepository pollRepository;

    public ClosePollImpl(PollRepository pollRepository) {
        this.pollRepository = pollRepository;
    }

    @Override
    public ClosePollOutput execute(ClosePollInput input) {
        Poll pollToClose = pollRepository.findById(input.pollId())
                .orElseThrow(() -> new EntityNotFoundException("Poll not found"));
        pollToClose.close();
        pollRepository.update(pollToClose);
        return toOutput(pollToClose);
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
