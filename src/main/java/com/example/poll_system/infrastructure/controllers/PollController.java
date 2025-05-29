package com.example.poll_system.infrastructure.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.poll_system.application.usecases.poll.CreatePoll;
import com.example.poll_system.application.usecases.poll.dto.CreatePollInput;
import com.example.poll_system.application.usecases.poll.dto.CreatePollOutput;
import com.example.poll_system.application.usecases.poll.impl.CreatePollImpl;
import com.example.poll_system.domain.entities.Poll;
import com.example.poll_system.domain.gateways.PollOptionRepository;
import com.example.poll_system.domain.gateways.PollRepository;
import com.example.poll_system.domain.gateways.UserRepository;

@RestController
@RequestMapping("/api/v1/polls")
public class PollController {

    private final UserRepository userRepository;
    private final PollOptionRepository pollOptionRepository;
    private final PollRepository pollRepository;

    public PollController(
            UserRepository userRepository,
            PollOptionRepository pollOptionRepository,
            PollRepository pollRepository) {
        this.userRepository = userRepository;
        this.pollOptionRepository = pollOptionRepository;
        this.pollRepository = pollRepository;
    }

    @PostMapping()
    public ResponseEntity<CreatePollOutput> createPoll(@RequestBody CreatePollInput input) {
        CreatePoll useCase = new CreatePollImpl(pollRepository, pollOptionRepository, userRepository);
        CreatePollOutput output = useCase.execute(input);
        return ResponseEntity.ok(output);
    }

    @GetMapping
    public ResponseEntity<List<Poll>> list() {
        return ResponseEntity.ok(pollRepository.findAll());
    }
}
