package com.example.poll_system.domain.entities.events;

import java.time.LocalDateTime;

import com.example.poll_system.domain.entities.DomainEvent;

public class VoteProcessedEvent extends DomainEvent {

    private final String userId;
    private final String userEmail;
    private final LocalDateTime voteDate;

    public VoteProcessedEvent(String userId, String userEmail, LocalDateTime voteDate) {
        super(VoteProcessedEvent.class.getSimpleName());
        this.userId = userId;
        this.userEmail = userEmail;
        this.voteDate = voteDate;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public LocalDateTime getVoteDate() {
        return voteDate;
    }
}
