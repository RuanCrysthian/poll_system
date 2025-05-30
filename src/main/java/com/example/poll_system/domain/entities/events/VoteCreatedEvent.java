package com.example.poll_system.domain.entities.events;

import com.example.poll_system.domain.entities.DomainEvent;
import com.example.poll_system.domain.exceptions.FieldIsRequiredException;

public class VoteCreatedEvent extends DomainEvent {

    private final String userId;
    private final String pollOptionId;

    public VoteCreatedEvent(String userId, String pollOptionId) {
        super(VoteCreatedEvent.class.getSimpleName());
        this.userId = userId;
        this.pollOptionId = pollOptionId;
        this.validate();
    }

    private void validate() {
        if (userId == null || userId.trim().isEmpty()) {
            throw new FieldIsRequiredException("userId is required");
        }
        if (pollOptionId == null || pollOptionId.trim().isEmpty()) {
            throw new FieldIsRequiredException("pollOptionId is required");
        }
    }

    public String getUserId() {
        return userId;
    }

    public String getPollOptionId() {
        return pollOptionId;
    }

}
