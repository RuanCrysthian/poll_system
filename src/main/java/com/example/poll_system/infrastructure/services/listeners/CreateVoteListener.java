package com.example.poll_system.infrastructure.services.listeners;

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

    @RabbitListener(queues = "${app.rabbitmq.vote-queue}")
    public void listen(VoteCreatedEvent message) {
        try {
            System.out.println("VoteQueueListener: recebida mensagem -> " + message.getUserId() + ", "
                    + message.getPollOptionId());

            ProcessVoteInput input = new ProcessVoteInput(message.getUserId(), message.getPollOptionId());
            processVote.execute(input);

        } catch (Exception e) {
            System.err.println("Erro ao processar voto: " + e.getMessage());
            throw e;
        }
    }
}
