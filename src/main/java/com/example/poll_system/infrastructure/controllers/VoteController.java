package com.example.poll_system.infrastructure.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.poll_system.application.usecases.vote.CreateVote;
import com.example.poll_system.application.usecases.vote.dto.CreateVoteInput;
import com.example.poll_system.application.usecases.vote.impl.SendVoteToQueue;
import com.example.poll_system.domain.entities.Vote;
import com.example.poll_system.domain.gateways.PollOptionRepository;
import com.example.poll_system.domain.gateways.PollRepository;
import com.example.poll_system.domain.gateways.UserRepository;
import com.example.poll_system.domain.gateways.VoteRepository;
import com.example.poll_system.infrastructure.services.EventPublisher;

@RestController
@RequestMapping("/api/v1/votes")
public class VoteController {

    private final VoteRepository voteRepository;
    private final UserRepository userRepository;
    private final PollOptionRepository pollOptionRepository;
    private final EventPublisher eventPublisher;
    private final PollRepository pollRepository;

    public VoteController(
            VoteRepository voteRepository,
            UserRepository userRepository,
            PollOptionRepository pollOptionRepository,
            EventPublisher eventPublisher,
            PollRepository pollRepository) {
        this.voteRepository = voteRepository;
        this.userRepository = userRepository;
        this.pollOptionRepository = pollOptionRepository;
        this.eventPublisher = eventPublisher;
        this.pollRepository = pollRepository;
    }

    @PostMapping()
    public ResponseEntity<Void> createVote(@RequestBody CreateVoteInput input) {
        CreateVote createVote = new SendVoteToQueue(pollOptionRepository, userRepository, pollRepository,
                eventPublisher);
        createVote.execute(input);
        return ResponseEntity.accepted().build();
    }

    @GetMapping()
    public ResponseEntity<List<Vote>> list() {
        return ResponseEntity.ok(voteRepository.findAll());
    }

}
