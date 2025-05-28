package com.example.poll_system.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.poll_system.domain.entities.events.VoteCreatedEvent;
import com.example.poll_system.domain.entities.events.VoteProcessedEvent;
import com.example.poll_system.infrastructure.services.handlers.SendEmailVoteProcessedHandler;
import com.example.poll_system.infrastructure.services.handlers.SendVoteQueueHandler;
import com.example.poll_system.infrastructure.services.impl.EventDispatcher;

@Configuration
public class EventDispatcherConfig {

    @Bean
    public EventDispatcher eventDispatcher(
            SendVoteQueueHandler sendVoteQueueHandler,
            SendEmailVoteProcessedHandler sendEmailVoteHandler) {
        EventDispatcher eventDispatcher = new EventDispatcher();
        eventDispatcher.registerHandler(VoteCreatedEvent.class, sendVoteQueueHandler);
        eventDispatcher.registerHandler(VoteProcessedEvent.class, sendEmailVoteHandler);
        return eventDispatcher;
    }
}
