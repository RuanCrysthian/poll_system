package com.example.poll_system.application.usecases.poll.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.poll_system.application.usecases.poll.CreatePoll;
import com.example.poll_system.application.usecases.poll.dto.CreatePollInput;
import com.example.poll_system.application.usecases.poll.dto.CreatePollOutput;
import com.example.poll_system.application.usecases.poll.dto.PollOptionOutput;
import com.example.poll_system.domain.entities.Poll;
import com.example.poll_system.domain.entities.PollOption;
import com.example.poll_system.domain.exceptions.BusinessRulesException;
import com.example.poll_system.domain.factories.PollFactory;
import com.example.poll_system.domain.factories.PollOptionFactory;
import com.example.poll_system.domain.gateways.PollOptionRepository;
import com.example.poll_system.domain.gateways.PollRepository;
import com.example.poll_system.domain.gateways.UserRepository;

@Service
public class CreatePollImpl implements CreatePoll {

    private final PollRepository pollRepository;
    private final PollOptionRepository pollOptionRepository;
    private final UserRepository userRepository;

    public CreatePollImpl(PollRepository pollRepository, PollOptionRepository pollOptionRepository,
            UserRepository userRepository) {
        this.pollRepository = pollRepository;
        this.pollOptionRepository = pollOptionRepository;
        this.userRepository = userRepository;
    }

    @Override
    public CreatePollOutput execute(CreatePollInput input) {
        validateInput(input);
        String pollId = UUID.randomUUID().toString();
        List<PollOption> pollOptions = input.options().stream()
                .map(p -> PollOptionFactory.create(p.description(), pollId))
                .toList();
        pollOptionRepository.saveAll(pollOptions);
        Poll poll = PollFactory.create(
                pollId,
                input.title(),
                input.description(),
                input.ownerId(),
                input.startDate(),
                input.endDate(),
                pollOptions);
        pollRepository.save(poll);
        return toOutput(poll);
    }

    private void validateInput(CreatePollInput input) {
        userRepository.findById(input.ownerId())
                .orElseThrow(() -> new BusinessRulesException("Owner not found."));
    }

    private CreatePollOutput toOutput(Poll poll) {
        return new CreatePollOutput(
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
