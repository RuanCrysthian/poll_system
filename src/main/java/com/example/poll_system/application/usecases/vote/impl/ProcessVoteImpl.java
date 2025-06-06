package com.example.poll_system.application.usecases.vote.impl;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.poll_system.application.usecases.vote.ProcessVote;
import com.example.poll_system.application.usecases.vote.dto.ProcessVoteInput;
import com.example.poll_system.application.usecases.vote.dto.ProcessVoteOutput;
import com.example.poll_system.domain.entities.PollOption;
import com.example.poll_system.domain.entities.User;
import com.example.poll_system.domain.entities.Vote;
import com.example.poll_system.domain.entities.events.VoteProcessedEvent;
import com.example.poll_system.domain.enums.VoteStatus;
import com.example.poll_system.domain.exceptions.EntityNotFoundException;
import com.example.poll_system.domain.factories.VoteFactory;
import com.example.poll_system.domain.gateways.PollOptionRepository;
import com.example.poll_system.domain.gateways.UserRepository;
import com.example.poll_system.domain.gateways.VoteRepository;
import com.example.poll_system.infrastructure.services.EventPublisher;

@Service
public class ProcessVoteImpl implements ProcessVote {

    private final VoteRepository voteRepository;
    private final UserRepository userRepository;
    private final PollOptionRepository pollOptionRepository;
    private final EventPublisher eventPublisher;

    public ProcessVoteImpl(
            VoteRepository voteRepository,
            UserRepository userRepository,
            PollOptionRepository pollOptionRepository,
            EventPublisher eventPublisher) {
        this.voteRepository = voteRepository;
        this.userRepository = userRepository;
        this.pollOptionRepository = pollOptionRepository;
        this.eventPublisher = eventPublisher;
    }

    private final Logger logger = LoggerFactory.getLogger(ProcessVoteImpl.class);

    @Override
    public ProcessVoteOutput execute(ProcessVoteInput input) {
        PollOption pollOption = pollOptionRepository.findById(input.pollOptionId())
                .orElseThrow(() -> new EntityNotFoundException("Poll option not found"));
        Vote vote = VoteFactory.create(input.userId(), input.pollOptionId(), pollOption.getPollId());
        vote.setStatus(VoteStatus.PROCESSED);
        voteRepository.save(vote);
        User user = getValidatedUser(vote.getUserId());
        eventPublisher.publish(new VoteProcessedEvent(user.getId(), user.getEmail().getEmail(), vote.getCreatedAt()));
        sendInfoLogMessageVoteProcessedEmailSent(user.getEmail().getEmail(), vote.getCreatedAt());
        sendInforLogMessageVoteProcessed(vote);
        return toOutput(vote);
    }

    private void sendInfoLogMessageVoteProcessedEmailSent(String email, LocalDateTime createdAt) {
        logger.info("Vote processed - Email sent to: {} at {}", email, createdAt);
    }

    private void sendInforLogMessageVoteProcessed(Vote vote) {
        logger.info(
                "Vote processed - Vote ID: {}, User ID: {}, Poll Option ID: {}, Poll ID: {}, Created At: {}",
                vote.getId(), vote.getUserId(), vote.getPollOptionId(), vote.getPollId(), vote.getCreatedAt());
    }

    private User getValidatedUser(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    sendWarningLogMessageUserNotFound(userId);
                    return new EntityNotFoundException("User not found");
                });
    }

    private void sendWarningLogMessageUserNotFound(String userId) {
        logger.warn("Vote processing failed - User not found: {}", userId);
    }

    private ProcessVoteOutput toOutput(Vote vote) {
        return new ProcessVoteOutput(
                vote.getId(),
                vote.getUserId(),
                vote.getPollOptionId(),
                vote.getPollId(),
                vote.getStatus().name(),
                vote.getCreatedAt());
    }

}
