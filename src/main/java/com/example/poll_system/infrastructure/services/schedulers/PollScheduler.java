package com.example.poll_system.infrastructure.services.schedulers;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.poll_system.application.usecases.poll.ActivePoll;
import com.example.poll_system.application.usecases.poll.ClosePoll;
import com.example.poll_system.application.usecases.poll.dto.ActivePollInput;
import com.example.poll_system.application.usecases.poll.dto.ClosePollInput;
import com.example.poll_system.domain.entities.Poll;
import com.example.poll_system.domain.enums.PollStatus;
import com.example.poll_system.domain.gateways.PollRepository;

@Component
public class PollScheduler {

    private final ActivePoll activePoll;
    private final ClosePoll closePoll;
    private final PollRepository pollRepository;

    public PollScheduler(ActivePoll activePoll, PollRepository pollRepository, ClosePoll closePoll) {
        this.activePoll = activePoll;
        this.pollRepository = pollRepository;
        this.closePoll = closePoll;
    }

    private final Logger logger = LoggerFactory.getLogger(PollScheduler.class);

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void open() {
        sendInfoLogMessageExecutingPollScheduler();
        List<Poll> scheduledPolls = pollRepository.findByStatus(PollStatus.SCHEDULED);
        LocalDateTime now = LocalDateTime.now();

        for (Poll poll : scheduledPolls) {
            if (!poll.getStartDate().isAfter(now)) {
                activePoll.execute(new ActivePollInput(poll.getId()));
                sendInfoLogMessageActivatingPoll(poll);
            }
        }
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void close() {
        sendInfoLogMessageExecutingPollScheduler();
        List<Poll> activePolls = pollRepository.findByStatus(PollStatus.OPEN);
        LocalDateTime now = LocalDateTime.now();

        for (Poll poll : activePolls) {
            if (!poll.getEndDate().isAfter(now)) {
                closePoll.execute(new ClosePollInput(poll.getId()));
                sendInfoLogMessageClosingPoll(poll);
            }
        }
    }

    private void sendInfoLogMessageExecutingPollScheduler() {
        logger.info("Executing PollScheduler at {}.", LocalDateTime.now());
    }

    private void sendInfoLogMessageActivatingPoll(Poll poll) {
        logger.info("Activating poll: pollId={} at {}",
                poll.getId(), LocalDateTime.now());
    }

    private void sendInfoLogMessageClosingPoll(Poll poll) {
        logger.info("Closing poll: pollId={} at {}", poll.getId(), LocalDateTime.now());
    }

}
