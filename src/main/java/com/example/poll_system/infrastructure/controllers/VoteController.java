package com.example.poll_system.infrastructure.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.poll_system.application.usecases.vote.CreateVote;
import com.example.poll_system.application.usecases.vote.FindVoteByIdUseCase;
import com.example.poll_system.application.usecases.vote.ListVoteUseCase;
import com.example.poll_system.application.usecases.vote.dto.CreateVoteInput;
import com.example.poll_system.application.usecases.vote.dto.FindVoteByIdInput;
import com.example.poll_system.application.usecases.vote.dto.FindVoteByIdOutput;
import com.example.poll_system.application.usecases.vote.dto.ListVoteOutput;
import com.example.poll_system.application.usecases.vote.impl.FindVoteById;
import com.example.poll_system.application.usecases.vote.impl.ListVotePageable;
import com.example.poll_system.application.usecases.vote.impl.SendVoteToQueue;
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

    // @GetMapping()
    // public ResponseEntity<List<Vote>> list() {
    // return ResponseEntity.ok(voteRepository.findAll());
    // }

    @GetMapping()
    public ResponseEntity<Page<ListVoteOutput>> list(Pageable pageable) {
        ListVoteUseCase listVoteUseCase = new ListVotePageable(voteRepository);
        return ResponseEntity.ok(listVoteUseCase.execute(pageable));
    }

    @GetMapping("/{voteId}")
    public ResponseEntity<FindVoteByIdOutput> findVoteById(@PathVariable String voteId) {
        FindVoteByIdUseCase findVoteByIdUseCase = new FindVoteById(voteRepository);
        return ResponseEntity.ok(findVoteByIdUseCase.execute(new FindVoteByIdInput(voteId)));
    }

}
