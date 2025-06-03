package com.example.poll_system.infrastructure.services.listeners;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.example.poll_system.application.usecases.user.SendEmailUseCase;
import com.example.poll_system.application.usecases.vote.dto.SendEmailInput;
import com.example.poll_system.domain.entities.events.VoteProcessedEvent;
import com.example.poll_system.domain.exceptions.FailedToSendMessageToQueueException;

@Component
public class SendEmailVoteProcessedListener {

    private final SendEmailUseCase sendEmailVoteProcessed;
    private static final String SUBJECT = "Voto Processado";
    private static final String BODY = "Seu voto foi processado com sucesso!";

    public SendEmailVoteProcessedListener(SendEmailUseCase sendEmailToUserUseCase) {
        this.sendEmailVoteProcessed = sendEmailToUserUseCase;
    }

    @RabbitListener(queues = "${app.rabbitmq.email-queue}")
    public void listen(VoteProcessedEvent event) {
        try {
            SendEmailInput input = new SendEmailInput(
                    event.getUserEmail(),
                    SUBJECT,
                    BODY);
            sendEmailVoteProcessed.execute(input);
        } catch (Exception e) {
            throw new FailedToSendMessageToQueueException("Erro ao enviar email");
        }
    }

}
