package com.example.poll_system.infrastructure.services.handlers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.poll_system.domain.entities.events.PollClosedEvent;
import com.example.poll_system.domain.gateways.MessageQueueGateway;
import com.example.poll_system.infrastructure.services.DomainEventHandler;

@Component
public class SendEmailPollClosedHandler implements DomainEventHandler<PollClosedEvent> {

    @Value("${app.rabbitmq.exchange}")
    private String exchangeName;

    @Value("${app.rabbitmq.email-poll-close.routing-key}")
    private String emailRoutingKey;

    private final MessageQueueGateway messageQueueGateway;

    public SendEmailPollClosedHandler(MessageQueueGateway messageQueueGateway) {
        this.messageQueueGateway = messageQueueGateway;
    }

    @Override
    public void handle(PollClosedEvent event) {
        System.out.println("SendEmailPollClosedHandler: Enviando email para o usuário "
                + event.getOwnerPollId() + " com o email " + event.getOwnerEmail()
                + " informando que a enquete " + event.getPollId() + " foi fechada.");
        messageQueueGateway.send(exchangeName, event, emailRoutingKey);
    }

}
