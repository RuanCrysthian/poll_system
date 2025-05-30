package com.example.poll_system.infrastructure.services.schedulers;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.poll_system.application.usecases.poll.ActivePoll;
import com.example.poll_system.application.usecases.poll.dto.ActivePollInput;
import com.example.poll_system.domain.entities.Poll;
import com.example.poll_system.domain.enums.PollStatus;
import com.example.poll_system.domain.gateways.PollRepository;

@Component
public class PollScheduler {

    private final ActivePoll activePoll;
    private final PollRepository pollRepository;

    public PollScheduler(ActivePoll activePoll, PollRepository pollRepository) {
        this.activePoll = activePoll;
        this.pollRepository = pollRepository;
    }

    @Scheduled(fixedRate = 60000)
    public void execute() {
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
}
