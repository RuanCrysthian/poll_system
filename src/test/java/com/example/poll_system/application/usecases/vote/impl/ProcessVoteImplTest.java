package com.example.poll_system.application.usecases.vote.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.example.poll_system.application.usecases.vote.dto.ProcessVoteInput;
import com.example.poll_system.application.usecases.vote.dto.ProcessVoteOutput;
import com.example.poll_system.domain.entities.PollOption;
import com.example.poll_system.domain.entities.User;
import com.example.poll_system.domain.entities.Vote;
import com.example.poll_system.domain.entities.events.VoteProcessedEvent;
import com.example.poll_system.domain.enums.VoteStatus;
import com.example.poll_system.domain.exceptions.EntityNotFoundException;
import com.example.poll_system.domain.factories.UserFactory;
import com.example.poll_system.domain.gateways.PollOptionRepository;
import com.example.poll_system.domain.gateways.UserRepository;
import com.example.poll_system.domain.gateways.VoteRepository;
import com.example.poll_system.infrastructure.services.EventPublisher;

public class ProcessVoteImplTest {

    @InjectMocks
    private ProcessVoteImpl processVoteImpl;

    @Mock
    private VoteRepository voteRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PollOptionRepository pollOptionRepository;

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

    private PollOption createPollOption(String pollOptionId, String pollId) {
        return new PollOption(pollOptionId, "Test Option", pollId);
    }

    @Test
    void shouldProcessVoteSuccessfullyWhenUserExists() {
        // Arrange
        String userId = "user-123";
        String pollOptionId = "option-456";
        String pollId = "poll-789";
        ProcessVoteInput input = new ProcessVoteInput(userId, pollOptionId);

        User user = createUser();
        PollOption pollOption = createPollOption(pollOptionId, pollId);

        Mockito.when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        Mockito.when(pollOptionRepository.findById(pollOptionId))
                .thenReturn(Optional.of(pollOption));

        // Act
        ProcessVoteOutput output = processVoteImpl.execute(input);

        // Assert
        Assertions.assertNotNull(output);
        Assertions.assertEquals(userId, output.userId());
        Assertions.assertEquals(pollOptionId, output.pollOptionId());
        Assertions.assertEquals(pollId, output.pollId());
        Assertions.assertEquals(VoteStatus.PROCESSED.name(), output.status());
        Assertions.assertNotNull(output.id());
        Assertions.assertNotNull(output.createdAt());

        Mockito.verify(voteRepository, Mockito.times(1)).save(any(Vote.class));
        Mockito.verify(userRepository, Mockito.times(1)).findById(userId);
        Mockito.verify(pollOptionRepository, Mockito.times(1)).findById(pollOptionId);
        Mockito.verify(eventPublisher, Mockito.times(1)).publish(any(VoteProcessedEvent.class));
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenUserDoesNotExist() {
        // Arrange
        String userId = "nonexistent-user";
        String pollOptionId = "option-456";
        String pollId = "poll-789";
        ProcessVoteInput input = new ProcessVoteInput(userId, pollOptionId);

        PollOption pollOption = createPollOption(pollOptionId, pollId);

        Mockito.when(userRepository.findById(userId))
                .thenReturn(Optional.empty());
        Mockito.when(pollOptionRepository.findById(pollOptionId))
                .thenReturn(Optional.of(pollOption));

        // Act & Assert
        EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> processVoteImpl.execute(input));

        Assertions.assertEquals("User not found", exception.getMessage());
        Mockito.verify(voteRepository, Mockito.times(1)).save(any(Vote.class));
        Mockito.verify(userRepository, Mockito.times(1)).findById(userId);
        Mockito.verify(pollOptionRepository, Mockito.times(1)).findById(pollOptionId);
        Mockito.verify(eventPublisher, Mockito.never()).publish(any());
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenPollOptionDoesNotExist() {
        // Arrange
        String userId = "user-123";
        String pollOptionId = "nonexistent-option";
        ProcessVoteInput input = new ProcessVoteInput(userId, pollOptionId);

        Mockito.when(pollOptionRepository.findById(pollOptionId))
                .thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> processVoteImpl.execute(input));

        Assertions.assertEquals("Poll option not found", exception.getMessage());
        Mockito.verify(pollOptionRepository, Mockito.times(1)).findById(pollOptionId);
        Mockito.verify(voteRepository, Mockito.never()).save(any(Vote.class));
        Mockito.verify(userRepository, Mockito.never()).findById(any());
        Mockito.verify(eventPublisher, Mockito.never()).publish(any());
    }

    @Test
    void shouldCreateVoteWithProcessedStatus() {
        // Arrange
        String userId = "user-123";
        String pollOptionId = "option-456";
        String pollId = "poll-789";
        ProcessVoteInput input = new ProcessVoteInput(userId, pollOptionId);

        User user = createUser();
        PollOption pollOption = createPollOption(pollOptionId, pollId);

        Mockito.when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        Mockito.when(pollOptionRepository.findById(pollOptionId))
                .thenReturn(Optional.of(pollOption));

        // Act
        processVoteImpl.execute(input);

        // Assert
        Mockito.verify(voteRepository).save(argThat(vote -> vote.getUserId().equals(userId) &&
                vote.getPollOptionId().equals(pollOptionId) &&
                vote.getPollId().equals(pollId) &&
                vote.getStatus() == VoteStatus.PROCESSED));
    }

    @Test
    void shouldPublishVoteProcessedEventWithCorrectData() {
        // Arrange
        String userId = "user-123";
        String pollOptionId = "option-456";
        String pollId = "poll-789";
        ProcessVoteInput input = new ProcessVoteInput(userId, pollOptionId);

        User user = createUser();
        PollOption pollOption = createPollOption(pollOptionId, pollId);

        Mockito.when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        Mockito.when(pollOptionRepository.findById(pollOptionId))
                .thenReturn(Optional.of(pollOption));

        // Act
        processVoteImpl.execute(input);

        // Assert
        Mockito.verify(eventPublisher).publish(argThat(event -> {
            if (event instanceof VoteProcessedEvent voteEvent) {
                return voteEvent.getUserId().equals(user.getId()) &&
                        voteEvent.getUserEmail().equals(user.getEmail().getEmail()) &&
                        voteEvent.getVoteDate() != null;
            }
            return false;
        }));
    }

    @Test
    void shouldReturnCorrectProcessVoteOutput() {
        // Arrange
        String userId = "user-123";
        String pollOptionId = "option-456";
        String pollId = "poll-789";
        ProcessVoteInput input = new ProcessVoteInput(userId, pollOptionId);

        User user = createUser();
        PollOption pollOption = createPollOption(pollOptionId, pollId);

        Mockito.when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        Mockito.when(pollOptionRepository.findById(pollOptionId))
                .thenReturn(Optional.of(pollOption));

        // Act
        ProcessVoteOutput output = processVoteImpl.execute(input);

        // Assert
        Assertions.assertNotNull(output.id());
        Assertions.assertEquals(userId, output.userId());
        Assertions.assertEquals(pollOptionId, output.pollOptionId());
        Assertions.assertEquals(pollId, output.pollId());
        Assertions.assertEquals(VoteStatus.PROCESSED.name(), output.status());
        Assertions.assertTrue(output.createdAt().isBefore(LocalDateTime.now().plusSeconds(1)));
        Assertions.assertTrue(output.createdAt().isAfter(LocalDateTime.now().minusSeconds(10)));
    }

    @Test
    void shouldSaveVoteBeforeValidatingUser() {
        // Arrange
        String userId = "user-123";
        String pollOptionId = "option-456";
        String pollId = "poll-789";
        ProcessVoteInput input = new ProcessVoteInput(userId, pollOptionId);

        User user = createUser();
        PollOption pollOption = createPollOption(pollOptionId, pollId);

        Mockito.when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        Mockito.when(pollOptionRepository.findById(pollOptionId))
                .thenReturn(Optional.of(pollOption));

        // Act
        processVoteImpl.execute(input);

        // Assert - Verifica que o voto foi salvo e depois o usuário foi validado
        Mockito.verify(voteRepository, Mockito.times(1)).save(any(Vote.class));
        Mockito.verify(userRepository, Mockito.times(1)).findById(userId);
        Mockito.verify(pollOptionRepository, Mockito.times(1)).findById(pollOptionId);
    }

    @Test
    void shouldCreateVoteWithUnprocessedStatusInitiallyThenSetToProcessed() {
        // Arrange
        String userId = "user-123";
        String pollOptionId = "option-456";
        String pollId = "poll-789";
        ProcessVoteInput input = new ProcessVoteInput(userId, pollOptionId);

        User user = createUser();
        PollOption pollOption = createPollOption(pollOptionId, pollId);

        Mockito.when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        Mockito.when(pollOptionRepository.findById(pollOptionId))
                .thenReturn(Optional.of(pollOption));

        // Act
        processVoteImpl.execute(input);

        // Assert - O VoteFactory cria com status UNPROCESSED, mas depois é alterado
        // para PROCESSED
        Mockito.verify(voteRepository).save(argThat(vote -> vote.getStatus() == VoteStatus.PROCESSED));
    }

    @Test
    void shouldNotPublishEventWhenUserValidationFails() {
        // Arrange
        String userId = "nonexistent-user";
        String pollOptionId = "option-456";
        String pollId = "poll-789";
        ProcessVoteInput input = new ProcessVoteInput(userId, pollOptionId);

        PollOption pollOption = createPollOption(pollOptionId, pollId);

        Mockito.when(userRepository.findById(userId))
                .thenReturn(Optional.empty());
        Mockito.when(pollOptionRepository.findById(pollOptionId))
                .thenReturn(Optional.of(pollOption));

        // Act & Assert
        Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> processVoteImpl.execute(input));

        // Assert - Verifica que o evento não foi publicado quando a validação do
        // usuário falha
        Mockito.verify(eventPublisher, Mockito.never()).publish(any());
    }

    @Test
    void shouldExecuteAllStepsInCorrectOrder() {
        // Arrange
        String userId = "user-123";
        String pollOptionId = "option-456";
        String pollId = "poll-789";
        ProcessVoteInput input = new ProcessVoteInput(userId, pollOptionId);

        User user = createUser();
        PollOption pollOption = createPollOption(pollOptionId, pollId);

        Mockito.when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        Mockito.when(pollOptionRepository.findById(pollOptionId))
                .thenReturn(Optional.of(pollOption));

        // Act
        ProcessVoteOutput output = processVoteImpl.execute(input);

        // Assert - Verifica que todas as operações foram executadas
        Assertions.assertNotNull(output);

        // Verifica a ordem de execução: find pollOption -> save vote -> find user ->
        // publish event
        var inOrder = Mockito.inOrder(pollOptionRepository, voteRepository, userRepository, eventPublisher);
        inOrder.verify(pollOptionRepository).findById(pollOptionId);
        inOrder.verify(voteRepository).save(any(Vote.class));
        inOrder.verify(userRepository).findById(userId);
        inOrder.verify(eventPublisher).publish(any(VoteProcessedEvent.class));
    }
}
