package com.example.poll_system.domain.entities.events;

import com.example.poll_system.domain.entities.DomainEvent;

public class VoteCreatedEvent extends DomainEvent {

    private final String userId;
    private final String pollOptionId;

    public VoteCreatedEvent(String userId, String pollOptionId) {
        super(VoteCreatedEvent.class.getSimpleName());
        this.userId = userId;
        this.pollOptionId = pollOptionId;
    }

    public String getUserId() {
        return userId;
    }

    public String getPollOptionId() {
        return pollOptionId;
    }

}
