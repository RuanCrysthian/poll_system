package com.example.poll_system.infrastructure.services.handlers;

import org.springframework.stereotype.Service;

import com.example.poll_system.domain.entities.events.VoteCreatedEvent;
import com.example.poll_system.domain.gateways.MessageQueueGateway;
import com.example.poll_system.infrastructure.services.DomainEventHandler;

@Service
public class SendVoteQueueHandler implements DomainEventHandler<VoteCreatedEvent> {

    private final static String EXCHANGE_NAME = "vote";

    private final static String ROUTING_KEY = "voting-key";

    private final MessageQueueGateway messageQueueGateway;

    public SendVoteQueueHandler(MessageQueueGateway messageQueueGateway) {
        this.messageQueueGateway = messageQueueGateway;
    }

    @Override
    public void handle(VoteCreatedEvent event) {
        messageQueueGateway.send(EXCHANGE_NAME, event, ROUTING_KEY);
        System.out.println(
                "SendVoteQueueHandler: VoteCreatedEvent enviado para a fila de mensageria. voteId: " + event.getUserId()
                        + ", pollOptionId: " + event.getPollOptionId());
    }

}
