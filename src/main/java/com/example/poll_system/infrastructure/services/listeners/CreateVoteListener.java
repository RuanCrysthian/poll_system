package com.example.poll_system.infrastructure.services.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.example.poll_system.application.usecases.vote.ProcessVote;
import com.example.poll_system.application.usecases.vote.dto.ProcessVoteInput;
import com.example.poll_system.domain.entities.events.VoteCreatedEvent;

@Component
public class CreateVoteListener {

    private final ProcessVote processVote;

    public CreateVoteListener(ProcessVote processVote) {
        this.processVote = processVote;
    }

    private final Logger logger = LoggerFactory.getLogger(CreateVoteListener.class);

    @RabbitListener(queues = "${app.rabbitmq.vote-queue}")
    public void listen(VoteCreatedEvent message) {
        try {
            ProcessVoteInput input = new ProcessVoteInput(message.getUserId(), message.getPollOptionId());
            processVote.execute(input);
            sendInfoLogMessageVoteProcessed(message);
        } catch (Exception e) {
            System.err.println("Erro ao processar voto: " + e.getMessage());
            throw e;
        }
    }

    private void sendInfoLogMessageVoteProcessed(VoteCreatedEvent message) {
        logger.info("Vote processed for user: {}", message.getUserId());
    }
}
