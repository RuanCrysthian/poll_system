package com.example.poll_system.domain.entities.events;

import java.time.LocalDateTime;

import com.example.poll_system.domain.entities.DomainEvent;
import com.example.poll_system.domain.exceptions.FieldIsRequiredException;

public class VoteProcessedEvent extends DomainEvent {

    private final String userId;
    private final String userEmail;
    private final LocalDateTime voteDate;

    public VoteProcessedEvent(String userId, String userEmail, LocalDateTime voteDate) {
        super(VoteProcessedEvent.class.getSimpleName());
        this.userId = userId;
        this.userEmail = userEmail;
        this.voteDate = voteDate;
        this.validate();
    }

    private void validate() {
        if (userId == null || userId.trim().isEmpty()) {
            throw new FieldIsRequiredException("userId is required");
        }
        if (userEmail == null || userEmail.trim().isEmpty()) {
            throw new FieldIsRequiredException("userEmail is required");
        }
        if (voteDate == null) {
            throw new FieldIsRequiredException("voteDate is required");
        }
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
