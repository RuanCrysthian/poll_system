package com.example.poll_system.application.usecases.user.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.example.poll_system.application.usecases.vote.dto.SendEmailInput;
import com.example.poll_system.infrastructure.services.MailSender;

public class SendEmailImplTest {

    @InjectMocks
    private SendEmailImpl sendEmailVoteProcessed;

    @Mock
    private MailSender mailSender;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldExecuteSuccessfullyAndCallMailSender() {
        // Arrange
        String email = "user@example.com";
        String subject = "Voto Processado";
        String body = "Seu voto foi processado com sucesso!";
        SendEmailInput input = new SendEmailInput(email, subject, body);

        // Act
        sendEmailVoteProcessed.execute(input);

        // Assert
        Mockito.verify(mailSender, Mockito.times(1)).send(email, subject, body);
    }

    @Test
    void shouldCallMailSenderWithCorrectParameters() {
        // Arrange
        String email = "test@example.com";
        String subject = "Test Subject";
        String body = "Test Body Content";
        SendEmailInput input = new SendEmailInput(email, subject, body);

        // Act
        sendEmailVoteProcessed.execute(input);

        // Assert
        Mockito.verify(mailSender).send(
                Mockito.eq(email),
                Mockito.eq(subject),
                Mockito.eq(body));
    }

    @Test
    void shouldExecuteWithDifferentEmailFormats() {
        // Arrange
        String email = "user.name+tag@domain.co.uk";
        String subject = "Important Notification";
        String body = "Your vote has been successfully processed.";
        SendEmailInput input = new SendEmailInput(email, subject, body);

        // Act
        sendEmailVoteProcessed.execute(input);

        // Assert
        Mockito.verify(mailSender, Mockito.times(1)).send(email, subject, body);
    }

    @Test
    void shouldExecuteWithLongSubjectAndBody() {
        // Arrange
        String email = "user@example.com";
        String subject = "This is a very long subject line that might be used in real scenarios with detailed information";
        String body = "This is a very detailed body content that includes multiple lines of text and comprehensive information about the vote processing status and additional details that might be relevant to the user.";
        SendEmailInput input = new SendEmailInput(email, subject, body);

        // Act
        sendEmailVoteProcessed.execute(input);

        // Assert
        Mockito.verify(mailSender, Mockito.times(1)).send(email, subject, body);
    }

    @Test
    void shouldExecuteWithEmptySubjectAndBody() {
        // Arrange
        String email = "user@example.com";
        String subject = "";
        String body = "";
        SendEmailInput input = new SendEmailInput(email, subject, body);

        // Act
        sendEmailVoteProcessed.execute(input);

        // Assert
        Mockito.verify(mailSender, Mockito.times(1)).send(email, subject, body);
    }

    @Test
    void shouldExecuteMultipleTimes() {
        // Arrange
        String email1 = "user1@example.com";
        String email2 = "user2@example.com";
        String subject = "Notification";
        String body = "Your vote has been processed.";

        SendEmailInput input1 = new SendEmailInput(email1, subject, body);
        SendEmailInput input2 = new SendEmailInput(email2, subject, body);

        // Act
        sendEmailVoteProcessed.execute(input1);
        sendEmailVoteProcessed.execute(input2);

        // Assert
        Mockito.verify(mailSender, Mockito.times(1)).send(email1, subject, body);
        Mockito.verify(mailSender, Mockito.times(1)).send(email2, subject, body);
        Mockito.verify(mailSender, Mockito.times(2)).send(Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString());
    }

    @Test
    void shouldExecuteWithSpecialCharactersInContent() {
        // Arrange
        String email = "user@example.com";
        String subject = "Confirmação: Voto Processado! 🎉";
        String body = "Olá! Seu voto foi processado com sucesso.\n\nDetalhes:\n- Status: ✅ Processado\n- Data: Hoje\n\nObrigado!";
        SendEmailInput input = new SendEmailInput(email, subject, body);

        // Act
        sendEmailVoteProcessed.execute(input);

        // Assert
        Mockito.verify(mailSender, Mockito.times(1)).send(email, subject, body);
    }

    @Test
    void shouldExecuteWithNumericEmail() {
        // Arrange
        String email = "12345@example.com";
        String subject = "Vote Processed";
        String body = "Your vote with ID 67890 has been processed.";
        SendEmailInput input = new SendEmailInput(email, subject, body);

        // Act
        sendEmailVoteProcessed.execute(input);

        // Assert
        Mockito.verify(mailSender, Mockito.times(1)).send(email, subject, body);
    }

    @Test
    void shouldNotThrowExceptionWhenMailSenderDoesNotThrow() {
        // Arrange
        String email = "user@example.com";
        String subject = "Subject";
        String body = "Body";
        SendEmailInput input = new SendEmailInput(email, subject, body);

        // Configurar o mock para não fazer nada (comportamento padrão)
        Mockito.doNothing().when(mailSender).send(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());

        // Act & Assert
        Assertions.assertDoesNotThrow(() -> sendEmailVoteProcessed.execute(input));
        Mockito.verify(mailSender, Mockito.times(1)).send(email, subject, body);
    }

    @Test
    void shouldPropagateExceptionWhenMailSenderThrows() {
        // Arrange
        String email = "user@example.com";
        String subject = "Subject";
        String body = "Body";
        SendEmailInput input = new SendEmailInput(email, subject, body);

        RuntimeException expectedException = new RuntimeException("Mail server unavailable");
        Mockito.doThrow(expectedException).when(mailSender).send(Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString());

        // Act & Assert
        RuntimeException exception = Assertions.assertThrows(
                RuntimeException.class,
                () -> sendEmailVoteProcessed.execute(input));

        Assertions.assertEquals("Mail server unavailable", exception.getMessage());
        Mockito.verify(mailSender, Mockito.times(1)).send(email, subject, body);
    }
}
