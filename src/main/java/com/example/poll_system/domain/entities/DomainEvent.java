package com.example.poll_system.domain.entities;

import java.time.LocalDateTime;

public abstract class DomainEvent {
    private final String eventName;
    private final LocalDateTime occurredAt;

    protected DomainEvent(String eventName) {
        this.eventName = eventName;
        this.occurredAt = LocalDateTime.now();
    }

    public String getEventName() {
        return eventName;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }
}
