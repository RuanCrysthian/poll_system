package com.example.poll_system.infrastructure.services.listeners;

import java.time.LocalDateTime;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.example.poll_system.application.usecases.vote.SendEmailVoteProcessedUseCase;
import com.example.poll_system.application.usecases.vote.dto.VoteProcessedEmailInput;
import com.example.poll_system.domain.entities.events.VoteProcessedEvent;
import com.example.poll_system.domain.exceptions.FailedToSendMessageToQueueException;

@Component
public class SendEmailListener {

    private final SendEmailVoteProcessedUseCase sendEmailVoteProcessed;
    private static final String SUBJECT = "Voto Processado";
    private static final String BODY = "Seu voto foi processado com sucesso!";

    public SendEmailListener(SendEmailVoteProcessedUseCase sendEmailToUserUseCase) {
        this.sendEmailVoteProcessed = sendEmailToUserUseCase;
    }

    @RabbitListener(queues = "${app.rabbitmq.email-queue}")
    public void listen(VoteProcessedEvent event) {
        try {
            System.out.println("Tentando enviar email para o usuário em " + LocalDateTime.now().toString());
            VoteProcessedEmailInput input = new VoteProcessedEmailInput(
                    event.getUserEmail(),
                    SUBJECT,
                    BODY);
            sendEmailVoteProcessed.execute(input);
            System.out.println("SendEmailListener: Enviando email para o usuário "
                    + event.getUserId() + " com o email " + event.getUserEmail()
                    + " informando que o voto foi processado.");

        } catch (Exception e) {
            throw new FailedToSendMessageToQueueException("Erro ao enviar email");
        }
    }
}
