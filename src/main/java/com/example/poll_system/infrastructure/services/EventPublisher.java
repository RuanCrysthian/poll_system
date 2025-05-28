package com.example.poll_system.infrastructure.services;

import com.example.poll_system.domain.entities.DomainEvent;

public interface EventPublisher {
    void publish(DomainEvent event);

}
