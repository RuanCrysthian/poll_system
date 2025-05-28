package com.example.poll_system.infrastructure.services.handlers;

import org.springframework.stereotype.Component;

import com.example.poll_system.domain.entities.events.VoteProcessedEvent;
import com.example.poll_system.infrastructure.services.DomainEventHandler;

@Component
public class SendEmailVoteProcessedHandler implements DomainEventHandler<VoteProcessedEvent> {

    @Override
    public void handle(VoteProcessedEvent event) {
        System.out.println("SendEmailVoteProcessedHandler: Enviando email para o usuário "
                + event.getUserId() + " com o email " + event.getUserEmail()
                + " informando que o voto foi processado.");
    }

}
