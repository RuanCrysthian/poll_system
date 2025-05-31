package com.example.poll_system.infrastructure.services.schedulers;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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

    @Scheduled(fixedRate = 60000)
    public void open() {
        System.out.println("Executing PollScheduler at " + LocalDateTime.now());
        List<Poll> scheduledPolls = pollRepository.findByStatus(PollStatus.SCHEDULED);
        LocalDateTime now = LocalDateTime.now();

        for (Poll poll : scheduledPolls) {
            if (!poll.getStartDate().isAfter(now)) {
                activePoll.execute(new ActivePollInput(poll.getId()));
                System.out.println("Activated poll: " + poll.getId() + " at " + now);
            }
        }
    }

    @Scheduled(fixedRate = 60000)
    public void close() {
        System.out.println("Executing PollScheduler at " + LocalDateTime.now());
        List<Poll> activePolls = pollRepository.findByStatus(PollStatus.OPEN);
        LocalDateTime now = LocalDateTime.now();

        for (Poll poll : activePolls) {
            if (!poll.getEndDate().isAfter(now)) {
                closePoll.execute(new ClosePollInput(poll.getId()));
                System.out.println("Closed poll: " + poll.getId() + " at " + now);
            }
        }
    }

}
