package com.example.poll_system.infrastructure.services;

import com.example.poll_system.domain.entities.DomainEvent;

public interface DomainEventHandler<T extends DomainEvent> {
    void handle(T event);
}
