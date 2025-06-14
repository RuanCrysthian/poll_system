package com.example.poll_system.application.usecases.vote.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.example.poll_system.application.usecases.vote.dto.CreateVoteInput;
import com.example.poll_system.domain.entities.Poll;
import com.example.poll_system.domain.entities.PollOption;
import com.example.poll_system.domain.entities.User;
import com.example.poll_system.domain.entities.events.VoteCreatedEvent;
import com.example.poll_system.domain.enums.PollStatus;
import com.example.poll_system.domain.exceptions.BusinessRulesException;
import com.example.poll_system.domain.factories.PollFactory;
import com.example.poll_system.domain.factories.UserFactory;
import com.example.poll_system.domain.gateways.PollOptionRepository;
import com.example.poll_system.domain.gateways.PollRepository;
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
        private PollRepository pollRepository;

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

        private Poll createPoll(PollStatus status) {
                PollOption option1 = createPollOption();
                PollOption option2 = new PollOption("poll-option-2", "Option 2", "poll-1");
                Poll poll = PollFactory.create(
                                "poll-1",
                                "Test Poll",
                                "Test Description",
                                "owner-1",
                                null, // startDate null para criar um poll aberto
                                LocalDateTime.now().plusDays(1),
                                List.of(option1, option2)); // Pelo menos duas opções são necessárias

                // Usar setStatus para alterar o status do poll
                poll.setStatus(status);

                return poll;
        }

        @Test
        void shouldExecuteSuccessfullyWhenUserAndPollOptionExist() {
                // Arrange
                String userId = "user-123";
                String pollOptionId = "option-456";
                CreateVoteInput input = new CreateVoteInput(userId, pollOptionId);

                User user = createUser();
                PollOption pollOption = createPollOption();
                Poll poll = createPoll(PollStatus.OPEN);

                Mockito.when(userRepository.findById(userId))
                                .thenReturn(Optional.of(user));
                Mockito.when(pollOptionRepository.findById(pollOptionId))
                                .thenReturn(Optional.of(pollOption));
                Mockito.when(pollRepository.findById(pollOption.getPollId()))
                                .thenReturn(Optional.of(poll));

                // Act
                sendVoteToQueue.execute(input);

                // Assert
                Mockito.verify(userRepository, Mockito.times(1)).findById(userId);
                Mockito.verify(pollOptionRepository, Mockito.times(1)).findById(pollOptionId);
                Mockito.verify(pollRepository, Mockito.times(1)).findById(pollOption.getPollId());
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
                                () -> sendVoteToQueue.execute(input));

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
                                () -> sendVoteToQueue.execute(input));

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
                Poll poll = createPoll(PollStatus.OPEN);

                Mockito.when(userRepository.findById(userId))
                                .thenReturn(Optional.of(user));
                Mockito.when(pollOptionRepository.findById(pollOptionId))
                                .thenReturn(Optional.of(pollOption));
                Mockito.when(pollRepository.findById(pollOption.getPollId()))
                                .thenReturn(Optional.of(poll));

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
                Poll poll = createPoll(PollStatus.OPEN);

                Mockito.when(userRepository.findById(userId))
                                .thenReturn(Optional.of(user));
                Mockito.when(pollOptionRepository.findById(pollOptionId))
                                .thenReturn(Optional.of(pollOption));
                Mockito.when(pollRepository.findById(pollOption.getPollId()))
                                .thenReturn(Optional.of(poll));

                // Act
                sendVoteToQueue.execute(input);

                // Assert - Verifica que as validações foram chamadas antes da publicação do
                // evento
                Mockito.verify(userRepository, Mockito.times(1)).findById(userId);
                Mockito.verify(pollOptionRepository, Mockito.times(1)).findById(pollOptionId);
                Mockito.verify(pollRepository, Mockito.times(1)).findById(pollOption.getPollId());
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
                                () -> sendVoteToQueue.execute(input));

                // Assert - Verifica que o evento não foi publicado quando a validação falha
                Mockito.verify(eventPublisher, Mockito.never()).publish(Mockito.any());
        }

        @Test
        void shouldThrowBusinessRulesExceptionWhenPollIsNotOpen() {
                // Arrange
                String userId = "user-123";
                String pollOptionId = "option-456";
                CreateVoteInput input = new CreateVoteInput(userId, pollOptionId);

                User user = createUser();
                PollOption pollOption = createPollOption();
                Poll poll = createPoll(PollStatus.CLOSED); // Poll fechado

                Mockito.when(userRepository.findById(userId))
                                .thenReturn(Optional.of(user));
                Mockito.when(pollOptionRepository.findById(pollOptionId))
                                .thenReturn(Optional.of(pollOption));
                Mockito.when(pollRepository.findById(pollOption.getPollId()))
                                .thenReturn(Optional.of(poll));

                // Act & Assert
                BusinessRulesException exception = Assertions.assertThrows(
                                BusinessRulesException.class,
                                () -> sendVoteToQueue.execute(input));

                Assertions.assertEquals("Poll is not open for voting", exception.getMessage());
                Mockito.verify(userRepository, Mockito.times(1)).findById(userId);
                Mockito.verify(pollOptionRepository, Mockito.times(1)).findById(pollOptionId);
                Mockito.verify(pollRepository, Mockito.times(1)).findById(pollOption.getPollId());
                Mockito.verify(eventPublisher, Mockito.never()).publish(Mockito.any());
        }

        @Test
        void shouldThrowBusinessRulesExceptionWhenPollIsScheduled() {
                // Arrange
                String userId = "user-123";
                String pollOptionId = "option-456";
                CreateVoteInput input = new CreateVoteInput(userId, pollOptionId);

                User user = createUser();
                PollOption pollOption = createPollOption();
                Poll poll = createPoll(PollStatus.SCHEDULED); // Poll agendado

                Mockito.when(userRepository.findById(userId))
                                .thenReturn(Optional.of(user));
                Mockito.when(pollOptionRepository.findById(pollOptionId))
                                .thenReturn(Optional.of(pollOption));
                Mockito.when(pollRepository.findById(pollOption.getPollId()))
                                .thenReturn(Optional.of(poll));

                // Act & Assert
                BusinessRulesException exception = Assertions.assertThrows(
                                BusinessRulesException.class,
                                () -> sendVoteToQueue.execute(input));

                Assertions.assertEquals("Poll is not open for voting", exception.getMessage());
                Mockito.verify(userRepository, Mockito.times(1)).findById(userId);
                Mockito.verify(pollOptionRepository, Mockito.times(1)).findById(pollOptionId);
                Mockito.verify(pollRepository, Mockito.times(1)).findById(pollOption.getPollId());
                Mockito.verify(eventPublisher, Mockito.never()).publish(Mockito.any());
        }

        @Test
        void shouldThrowBusinessRulesExceptionWhenPollIsPaused() {
                // Arrange
                String userId = "user-123";
                String pollOptionId = "option-456";
                CreateVoteInput input = new CreateVoteInput(userId, pollOptionId);

                User user = createUser();
                PollOption pollOption = createPollOption();
                Poll poll = createPoll(PollStatus.PAUSED); // Poll pausado

                Mockito.when(userRepository.findById(userId))
                                .thenReturn(Optional.of(user));
                Mockito.when(pollOptionRepository.findById(pollOptionId))
                                .thenReturn(Optional.of(pollOption));
                Mockito.when(pollRepository.findById(pollOption.getPollId()))
                                .thenReturn(Optional.of(poll));

                // Act & Assert
                BusinessRulesException exception = Assertions.assertThrows(
                                BusinessRulesException.class,
                                () -> sendVoteToQueue.execute(input));

                Assertions.assertEquals("Poll is not open for voting", exception.getMessage());
                Mockito.verify(userRepository, Mockito.times(1)).findById(userId);
                Mockito.verify(pollOptionRepository, Mockito.times(1)).findById(pollOptionId);
                Mockito.verify(pollRepository, Mockito.times(1)).findById(pollOption.getPollId());
                Mockito.verify(eventPublisher, Mockito.never()).publish(Mockito.any());
        }

        @Test
        void shouldThrowBusinessRulesExceptionWhenPollIsCanceled() {
                // Arrange
                String userId = "user-123";
                String pollOptionId = "option-456";
                CreateVoteInput input = new CreateVoteInput(userId, pollOptionId);

                User user = createUser();
                PollOption pollOption = createPollOption();
                Poll poll = createPoll(PollStatus.CANCELED); // Poll cancelado

                Mockito.when(userRepository.findById(userId))
                                .thenReturn(Optional.of(user));
                Mockito.when(pollOptionRepository.findById(pollOptionId))
                                .thenReturn(Optional.of(pollOption));
                Mockito.when(pollRepository.findById(pollOption.getPollId()))
                                .thenReturn(Optional.of(poll));

                // Act & Assert
                BusinessRulesException exception = Assertions.assertThrows(
                                BusinessRulesException.class,
                                () -> sendVoteToQueue.execute(input));

                Assertions.assertEquals("Poll is not open for voting", exception.getMessage());
                Mockito.verify(userRepository, Mockito.times(1)).findById(userId);
                Mockito.verify(pollOptionRepository, Mockito.times(1)).findById(pollOptionId);
                Mockito.verify(pollRepository, Mockito.times(1)).findById(pollOption.getPollId());
                Mockito.verify(eventPublisher, Mockito.never()).publish(Mockito.any());
        }

        @Test
        void shouldThrowBusinessRulesExceptionWhenPollNotFound() {
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
                Mockito.when(pollRepository.findById(pollOption.getPollId()))
                                .thenReturn(Optional.empty()); // Poll não encontrado

                // Act & Assert
                BusinessRulesException exception = Assertions.assertThrows(
                                BusinessRulesException.class,
                                () -> sendVoteToQueue.execute(input));

                Assertions.assertEquals("Poll not found", exception.getMessage());
                Mockito.verify(userRepository, Mockito.times(1)).findById(userId);
                Mockito.verify(pollOptionRepository, Mockito.times(1)).findById(pollOptionId);
                Mockito.verify(pollRepository, Mockito.times(1)).findById(pollOption.getPollId());
                Mockito.verify(eventPublisher, Mockito.never()).publish(Mockito.any());
        }
}
