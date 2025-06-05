package com.example.poll_system.application.usecases.poll.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.poll_system.application.usecases.poll.ListPollUseCase;
import com.example.poll_system.application.usecases.poll.dto.ListPollOutput;
import com.example.poll_system.domain.entities.Poll;
import com.example.poll_system.domain.gateways.PollRepository;

public class ListPollPageable implements ListPollUseCase {

    private final PollRepository pollRepository;

    public ListPollPageable(PollRepository pollRepository) {
        this.pollRepository = pollRepository;
    }

    @Override
    public Page<ListPollOutput> execute(Pageable pageable) {
        Page<Poll> pollsPage = pollRepository.findAll(pageable);
        return pollsPage.map(poll -> new ListPollOutput(
                poll.getId(),
                poll.getTitle(),
                poll.getDescription(),
                poll.getOwnerId(),
                poll.getStartDate(),
                poll.getEndDate(),
                poll.getStatus().name()));
    }

}
