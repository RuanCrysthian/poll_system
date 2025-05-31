package com.example.poll_system.infrastructure.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.poll_system.application.usecases.poll.CreatePoll;
import com.example.poll_system.application.usecases.poll.PollStatistics;
import com.example.poll_system.application.usecases.poll.dto.CreatePollInput;
import com.example.poll_system.application.usecases.poll.dto.CreatePollOutput;
import com.example.poll_system.application.usecases.poll.dto.PollStatisticsInput;
import com.example.poll_system.application.usecases.poll.dto.PollStatisticsOutput;
import com.example.poll_system.application.usecases.poll.impl.CreatePollImpl;
import com.example.poll_system.application.usecases.poll.impl.PollStatisticsImpl;
import com.example.poll_system.domain.entities.Poll;
import com.example.poll_system.domain.gateways.PollOptionRepository;
import com.example.poll_system.domain.gateways.PollRepository;
import com.example.poll_system.domain.gateways.UserRepository;
import com.example.poll_system.domain.gateways.VoteRepository;

@RestController
@RequestMapping("/api/v1/polls")
public class PollController {

    private final UserRepository userRepository;
    private final PollOptionRepository pollOptionRepository;
    private final PollRepository pollRepository;
    private final VoteRepository voteRepository;

    public PollController(
            UserRepository userRepository,
            PollOptionRepository pollOptionRepository,
            PollRepository pollRepository,
            VoteRepository voteRepository) {
        this.userRepository = userRepository;
        this.pollOptionRepository = pollOptionRepository;
        this.pollRepository = pollRepository;
        this.voteRepository = voteRepository;
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

    @GetMapping("/{pollId}/statistics")
    public ResponseEntity<PollStatisticsOutput> getPollStatistics(@PathVariable String pollId) {
        PollStatisticsInput input = new PollStatisticsInput(pollId);
        PollStatistics pollStatistics = new PollStatisticsImpl(pollRepository, voteRepository);
        PollStatisticsOutput output = pollStatistics.getPollStatistics(input);
        return ResponseEntity.ok(output);
    }
}
