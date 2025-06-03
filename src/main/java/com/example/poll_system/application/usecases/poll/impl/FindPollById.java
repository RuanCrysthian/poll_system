package com.example.poll_system.application.usecases.poll.impl;

import com.example.poll_system.application.usecases.poll.FindPollByIdUseCase;
import com.example.poll_system.application.usecases.poll.dto.FindPollByIdInput;
import com.example.poll_system.application.usecases.poll.dto.FindPollByIdOutput;
import com.example.poll_system.application.usecases.poll.dto.PollOptionOutput;
import com.example.poll_system.domain.entities.Poll;
import com.example.poll_system.domain.exceptions.EntityNotFoundException;
import com.example.poll_system.domain.gateways.PollRepository;

public class FindPollById implements FindPollByIdUseCase {

    private final PollRepository pollRepository;

    public FindPollById(PollRepository pollRepository) {
        this.pollRepository = pollRepository;
    }

    @Override
    public FindPollByIdOutput execute(FindPollByIdInput input) {
        Poll poll = pollRepository.findById(input.pollId())
                .orElseThrow(() -> new EntityNotFoundException("Poll not found"));
        return new FindPollByIdOutput(
                poll.getId(),
                poll.getTitle(),
                poll.getDescription(),
                poll.getOwnerId(),
                poll.getStartDate(),
                poll.getEndDate(),
                poll.getStatus().name(),
                poll.getOptions().stream()
                        .map(option -> new PollOptionOutput(
                                option.getId(),
                                option.getDescription()))
                        .toList());
    }

}
