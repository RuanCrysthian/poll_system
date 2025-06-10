package com.example.poll_system.infrastructure.services.handlers;

import org.springframework.stereotype.Component;

import com.example.poll_system.domain.entities.events.VoteProcessedEvent;
import com.example.poll_system.domain.gateways.MessageQueueGateway;
import com.example.poll_system.infrastructure.services.DomainEventHandler;

@Component
public class SendEmailVoteProcessedHandler implements DomainEventHandler<VoteProcessedEvent> {

    private final static String EXCHANGE_NAME = "vote";
    private final static String ROUTING_KEY = "email-key";

    private final MessageQueueGateway messageQueueGateway;

    public SendEmailVoteProcessedHandler(MessageQueueGateway messageQueueGateway) {
        this.messageQueueGateway = messageQueueGateway;
    }

    @Override
    public void handle(VoteProcessedEvent event) {
        messageQueueGateway.send(EXCHANGE_NAME, event, ROUTING_KEY);
    }

}
