package com.example.poll_system.infrastructure.services.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final Logger logger = LoggerFactory.getLogger(SendEmailVoteProcessedListener.class);

    @RabbitListener(queues = "${app.rabbitmq.email-queue}")
    public void listen(VoteProcessedEvent event) {
        try {
            SendEmailInput input = new SendEmailInput(
                    event.getUserEmail(),
                    SUBJECT,
                    BODY);
            sendEmailVoteProcessed.execute(input);
            sendInfoLogMessageEmailSent(event);
        } catch (Exception e) {
            logger.error("Failed to send email for vote processed event: {}", e.getMessage());
            throw new FailedToSendMessageToQueueException("Erro ao enviar email");
        }
    }

    private void sendInfoLogMessageEmailSent(VoteProcessedEvent event) {
        logger.info("Email Vote Processed sent to user: {} at {}", event.getUserEmail(), event.getOccurredAt());
    }

}
