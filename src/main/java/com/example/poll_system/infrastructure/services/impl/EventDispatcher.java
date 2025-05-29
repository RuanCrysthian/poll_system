package com.example.poll_system.infrastructure.services.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.poll_system.domain.entities.DomainEvent;
import com.example.poll_system.infrastructure.services.DomainEventHandler;
import com.example.poll_system.infrastructure.services.EventPublisher;

@Service
public class EventDispatcher implements EventPublisher {

    @SuppressWarnings("rawtypes")
    private final Map<Class<? extends DomainEvent>, List<DomainEventHandler>> handlers = new HashMap<>();

    public <T extends DomainEvent> void registerHandler(Class<T> eventType, DomainEventHandler<T> handler) {
        handlers.computeIfAbsent(eventType, k -> new ArrayList<>()).add(handler);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void publish(DomainEvent event) {
        List<DomainEventHandler> eventHandlers = handlers.getOrDefault(event.getClass(), List.of());
        for (DomainEventHandler handler : eventHandlers) {
            handler.handle(event);
        }
    }

}
