package com.example.poll_system.application.usecases.vote.impl;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.example.poll_system.application.usecases.vote.dto.CreateVoteInput;
import com.example.poll_system.domain.entities.PollOption;
import com.example.poll_system.domain.entities.User;
import com.example.poll_system.domain.entities.events.VoteCreatedEvent;
import com.example.poll_system.domain.exceptions.BusinessRulesException;
import com.example.poll_system.domain.factories.UserFactory;
import com.example.poll_system.domain.gateways.PollOptionRepository;
import com.example.poll_system.domain.gateways.UserRepository;
import com.example.poll_system.infrastructure.services.EventPublisher;

public class SendVoteToQueueTest {

    @InjectMocks
    private SendVoteToQueue sendVoteToQueue;

    @Mock
    private PollOptionRepository pollOptionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EventPublisher eventPublisher;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private User createUser() {
        return UserFactory.create(
                "John Doe",
                "05938337089",
                "john.doe@email.com",
                "QAZ123qaz*",
                "admin",
                "urlImageProfile");
    }

    private PollOption createPollOption() {
        return new PollOption("poll-option-1", "Option 1", "poll-1");
    }

    @Test
    void shouldExecuteSuccessfullyWhenUserAndPollOptionExist() {
        // Arrange
        String userId = "user-123";
        String pollOptionId = "option-456";
        CreateVoteInput input = new CreateVoteInput(userId, pollOptionId);
        
        User user = createUser();
        PollOption pollOption = createPollOption();
        
        Mockito.when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        Mockito.when(pollOptionRepository.findById(pollOptionId))
                .thenReturn(Optional.of(pollOption));

        // Act
        sendVoteToQueue.execute(input);

        // Assert
        Mockito.verify(userRepository, Mockito.times(1)).findById(userId);
        Mockito.verify(pollOptionRepository, Mockito.times(1)).findById(pollOptionId);
        Mockito.verify(eventPublisher, Mockito.times(1)).publish(Mockito.any(VoteCreatedEvent.class));
    }

    @Test
    void shouldThrowBusinessRulesExceptionWhenUserDoesNotExist() {
        // Arrange
        String userId = "nonexistent-user";
        String pollOptionId = "option-456";
        CreateVoteInput input = new CreateVoteInput(userId, pollOptionId);
        
        Mockito.when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        // Act & Assert
        BusinessRulesException exception = Assertions.assertThrows(
                BusinessRulesException.class,
                () -> sendVoteToQueue.execute(input)
        );
        
        Assertions.assertEquals("User not found", exception.getMessage());
        Mockito.verify(userRepository, Mockito.times(1)).findById(userId);
        Mockito.verify(pollOptionRepository, Mockito.never()).findById(Mockito.anyString());
        Mockito.verify(eventPublisher, Mockito.never()).publish(Mockito.any());
    }

    @Test
    void shouldThrowBusinessRulesExceptionWhenPollOptionDoesNotExist() {
        // Arrange
        String userId = "user-123";
        String pollOptionId = "nonexistent-option";
        CreateVoteInput input = new CreateVoteInput(userId, pollOptionId);
        
        User user = createUser();
        
        Mockito.when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        Mockito.when(pollOptionRepository.findById(pollOptionId))
                .thenReturn(Optional.empty());

        // Act & Assert
        BusinessRulesException exception = Assertions.assertThrows(
                BusinessRulesException.class,
                () -> sendVoteToQueue.execute(input)
        );
        
        Assertions.assertEquals("Poll option not found", exception.getMessage());
        Mockito.verify(userRepository, Mockito.times(1)).findById(userId);
        Mockito.verify(pollOptionRepository, Mockito.times(1)).findById(pollOptionId);
        Mockito.verify(eventPublisher, Mockito.never()).publish(Mockito.any());
    }

    @Test
    void shouldPublishVoteCreatedEventWithCorrectData() {
        // Arrange
        String userId = "user-123";
        String pollOptionId = "option-456";
        CreateVoteInput input = new CreateVoteInput(userId, pollOptionId);
        
        User user = createUser();
        PollOption pollOption = createPollOption();
        
        Mockito.when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        Mockito.when(pollOptionRepository.findById(pollOptionId))
                .thenReturn(Optional.of(pollOption));

        // Act
        sendVoteToQueue.execute(input);

        // Assert
        Mockito.verify(eventPublisher).publish(Mockito.argThat(event -> {
            if (event instanceof VoteCreatedEvent voteEvent) {
                return voteEvent.getUserId().equals(userId) && 
                       voteEvent.getPollOptionId().equals(pollOptionId);
            }
            return false;
        }));
    }

    @Test
    void shouldValidateInputBeforePublishingEvent() {
        // Arrange
        String userId = "user-123";
        String pollOptionId = "option-456";
        CreateVoteInput input = new CreateVoteInput(userId, pollOptionId);
        
        User user = createUser();
        PollOption pollOption = createPollOption();
        
        Mockito.when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        Mockito.when(pollOptionRepository.findById(pollOptionId))
                .thenReturn(Optional.of(pollOption));

        // Act
        sendVoteToQueue.execute(input);

        // Assert - Verifica que as validações foram chamadas antes da publicação do evento
        Mockito.verify(userRepository, Mockito.times(1)).findById(userId);
        Mockito.verify(pollOptionRepository, Mockito.times(1)).findById(pollOptionId);
        Mockito.verify(eventPublisher, Mockito.times(1)).publish(Mockito.any(VoteCreatedEvent.class));
    }

    @Test
    void shouldNotPublishEventWhenValidationFails() {
        // Arrange
        String userId = "user-123";
        String pollOptionId = "nonexistent-option";
        CreateVoteInput input = new CreateVoteInput(userId, pollOptionId);
        
        User user = createUser();
        
        Mockito.when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        Mockito.when(pollOptionRepository.findById(pollOptionId))
                .thenReturn(Optional.empty());

        // Act & Assert
        Assertions.assertThrows(
                BusinessRulesException.class,
                () -> sendVoteToQueue.execute(input)
        );
        
        // Assert - Verifica que o evento não foi publicado quando a validação falha
        Mockito.verify(eventPublisher, Mockito.never()).publish(Mockito.any());
    }
}
