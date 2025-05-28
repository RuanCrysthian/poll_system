package com.example.poll_system.application.usecases.poll.impl;

import java.util.ArrayList;
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
        // adicionar cada pollOption no banco de dados
        String pollId = UUID.randomUUID().toString();
        List<PollOption> pollOptions = new ArrayList<>();
        input.options().forEach(pollOption -> {
            PollOption option = PollOptionFactory.create(pollOption.description(), pollId);
            pollOptionRepository.save(option);
            pollOptions.add(option);
        });
        // criar a poll
        Poll poll = PollFactory.create(
                pollId,
                input.title(),
                input.description(),
                input.ownerId(), input.startDate(),
                input.endDate(),
                pollOptions);
        pollRepository.save(poll);
        // criar o output
        CreatePollOutput output = new CreatePollOutput(
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
        return output;
    }

    private void validateInput(CreatePollInput input) {
        if (!userRepository.findById(input.ownerId()).isPresent()) {
            throw new BusinessRulesException("aowner not found");
        }
    }

}
