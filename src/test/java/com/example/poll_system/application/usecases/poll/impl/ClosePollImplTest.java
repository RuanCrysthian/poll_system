package com.example.poll_system.application.usecases.poll.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.poll_system.application.usecases.poll.dto.ClosePollInput;
import com.example.poll_system.application.usecases.poll.dto.ClosePollOutput;
import com.example.poll_system.domain.entities.Poll;
import com.example.poll_system.domain.entities.PollOption;
import com.example.poll_system.domain.enums.PollStatus;
import com.example.poll_system.domain.exceptions.BusinessRulesException;
import com.example.poll_system.domain.exceptions.EntityNotFoundException;
import com.example.poll_system.domain.gateways.PollRepository;

public class ClosePollImplTest {

    @InjectMocks
    private ClosePollImpl closePollImpl;

    @Mock
    private PollRepository pollRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Poll createOpenPoll() {
        String pollId = "poll-123";
        String title = "Test Poll";
        String description = "Test poll description";
        String ownerId = "owner-123";
        LocalDateTime endDate = LocalDateTime.now().plusDays(2);
        List<PollOption> options = List.of(
                new PollOption("option-1", "Option 1", pollId),
                new PollOption("option-2", "Option 2", pollId));

        return Poll.createOpenPoll(pollId, title, description, ownerId, endDate, options);
    }

    private Poll createScheduledPoll() {
        String pollId = "poll-456";
        String title = "Scheduled Poll";
        String description = "Scheduled poll description";
        String ownerId = "owner-456";
        LocalDateTime startDate = LocalDateTime.now().plusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(3);
        List<PollOption> options = List.of(
                new PollOption("option-3", "Option A", pollId),
                new PollOption("option-4", "Option B", pollId));

        return Poll.createScheduledPoll(pollId, title, description, ownerId, startDate, endDate, options);
    }

    @Test
    void shouldClosePollSuccessfully() {
        // Given
        String pollId = "poll-123";
        ClosePollInput input = new ClosePollInput(pollId);
        Poll openPoll = createOpenPoll();

        when(pollRepository.findById(pollId)).thenReturn(Optional.of(openPoll));

        // When
        ClosePollOutput output = closePollImpl.execute(input);

        // Then
        assertNotNull(output);
        assertEquals(pollId, output.pollId());
        assertEquals("Test Poll", output.title());
        assertEquals("Test poll description", output.description());
        assertEquals("owner-123", output.ownerId());
        assertEquals(PollStatus.CLOSED.name(), output.status());
        assertNotNull(output.startDate());
        assertNotNull(output.endDate());
        assertEquals(2, output.options().size());
        assertEquals("option-1", output.options().get(0).pollOptionId());
        assertEquals("Option 1", output.options().get(0).description());
        assertEquals("option-2", output.options().get(1).pollOptionId());
        assertEquals("Option 2", output.options().get(1).description());

        verify(pollRepository, times(1)).findById(pollId);
        verify(pollRepository, times(1)).update(any(Poll.class));
    }

    @Test
    void shouldThrowExceptionWhenPollNotFound() {
        // Given
        String pollId = "nonexistent-poll";
        ClosePollInput input = new ClosePollInput(pollId);

        when(pollRepository.findById(pollId)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            closePollImpl.execute(input);
        });

        assertEquals("Poll not found", exception.getMessage());
        verify(pollRepository, times(1)).findById(pollId);
        verify(pollRepository, times(0)).update(any(Poll.class));
    }

    @Test
    void shouldThrowExceptionWhenTryingToCloseScheduledPoll() {
        // Given
        String pollId = "poll-456";
        ClosePollInput input = new ClosePollInput(pollId);
        Poll scheduledPoll = createScheduledPoll();

        when(pollRepository.findById(pollId)).thenReturn(Optional.of(scheduledPoll));

        // When & Then
        BusinessRulesException exception = assertThrows(BusinessRulesException.class, () -> {
            closePollImpl.execute(input);
        });

        assertEquals("Poll can only be closed if it is open", exception.getMessage());
        verify(pollRepository, times(1)).findById(pollId);
        verify(pollRepository, times(0)).update(any(Poll.class));
    }

    @Test
    void shouldThrowExceptionWhenTryingToCloseAlreadyClosedPoll() {
        // Given
        String pollId = "poll-closed";
        ClosePollInput input = new ClosePollInput(pollId);
        Poll openPoll = createOpenPoll();

        // Simulate a closed poll by opening and then closing it
        openPoll.close();

        when(pollRepository.findById(pollId)).thenReturn(Optional.of(openPoll));

        // When & Then
        BusinessRulesException exception = assertThrows(BusinessRulesException.class, () -> {
            closePollImpl.execute(input);
        });

        assertEquals("Poll can only be closed if it is open", exception.getMessage());
        verify(pollRepository, times(1)).findById(pollId);
        verify(pollRepository, times(0)).update(any(Poll.class));
    }

    @Test
    void shouldReturnCorrectOutputFormat() {
        // Given
        String pollId = "poll-123";
        ClosePollInput input = new ClosePollInput(pollId);
        Poll openPoll = createOpenPoll();

        when(pollRepository.findById(pollId)).thenReturn(Optional.of(openPoll));

        // When
        ClosePollOutput output = closePollImpl.execute(input);

        // Then
        assertNotNull(output);
        assertNotNull(output.pollId());
        assertNotNull(output.title());
        assertNotNull(output.description());
        assertNotNull(output.ownerId());
        assertNotNull(output.status());
        assertNotNull(output.startDate());
        assertNotNull(output.endDate());
        assertNotNull(output.options());
        assertEquals(2, output.options().size());

        // Verify option format
        output.options().forEach(option -> {
            assertNotNull(option.pollOptionId());
            assertNotNull(option.description());
        });

        verify(pollRepository, times(1)).findById(pollId);
        verify(pollRepository, times(1)).update(any(Poll.class));
    }

    @Test
    void shouldHandleNullPollId() {
        // Given
        ClosePollInput input = new ClosePollInput(null);

        when(pollRepository.findById(null)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            closePollImpl.execute(input);
        });

        assertEquals("Poll not found", exception.getMessage());
        verify(pollRepository, times(1)).findById(null);
    }

    @Test
    void shouldHandleEmptyPollId() {
        // Given
        String emptyPollId = "";
        ClosePollInput input = new ClosePollInput(emptyPollId);

        when(pollRepository.findById(emptyPollId)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            closePollImpl.execute(input);
        });

        assertEquals("Poll not found", exception.getMessage());
        verify(pollRepository, times(1)).findById(emptyPollId);
    }

    @Test
    void shouldClosePollWithMultipleOptions() {
        // Given
        String pollId = "poll-multiple-options";
        String title = "Poll with Multiple Options";
        String description = "A poll with many options";
        String ownerId = "owner-789";
        LocalDateTime endDate = LocalDateTime.now().plusDays(3);
        List<PollOption> options = List.of(
                new PollOption("option-1", "Option A", pollId),
                new PollOption("option-2", "Option B", pollId),
                new PollOption("option-3", "Option C", pollId),
                new PollOption("option-4", "Option D", pollId));

        Poll pollWithMultipleOptions = Poll.createOpenPoll(pollId, title, description, ownerId, endDate, options);
        ClosePollInput input = new ClosePollInput(pollId);

        when(pollRepository.findById(pollId)).thenReturn(Optional.of(pollWithMultipleOptions));

        // When
        ClosePollOutput output = closePollImpl.execute(input);

        // Then
        assertNotNull(output);
        assertEquals(pollId, output.pollId());
        assertEquals(title, output.title());
        assertEquals(description, output.description());
        assertEquals(ownerId, output.ownerId());
        assertEquals(PollStatus.CLOSED.name(), output.status());
        assertEquals(4, output.options().size());

        verify(pollRepository, times(1)).findById(pollId);
        verify(pollRepository, times(1)).update(any(Poll.class));
    }

    @Test
    void shouldMaintainPollDataIntegrityAfterClosing() {
        // Given
        String pollId = "poll-data-integrity";
        ClosePollInput input = new ClosePollInput(pollId);
        Poll originalPoll = createOpenPoll();
        String originalTitle = originalPoll.getTitle();
        String originalDescription = originalPoll.getDescription();
        String originalOwnerId = originalPoll.getOwnerId();
        LocalDateTime originalStartDate = originalPoll.getStartDate();
        int originalOptionsCount = originalPoll.getOptions().size();

        when(pollRepository.findById(pollId)).thenReturn(Optional.of(originalPoll));

        // When
        ClosePollOutput output = closePollImpl.execute(input);

        // Then
        assertEquals(originalTitle, output.title());
        assertEquals(originalDescription, output.description());
        assertEquals(originalOwnerId, output.ownerId());
        assertEquals(originalStartDate, output.startDate());
        assertEquals(originalOptionsCount, output.options().size());

        // Status should change to CLOSED
        assertEquals(PollStatus.CLOSED.name(), output.status());

        // End date should be updated (not the original one)
        assertNotNull(output.endDate());

        verify(pollRepository, times(1)).findById(pollId);
        verify(pollRepository, times(1)).update(any(Poll.class));
    }

    @Test
    void shouldVerifyRepositoryInteractions() {
        // Given
        String pollId = "poll-verification";
        ClosePollInput input = new ClosePollInput(pollId);
        Poll openPoll = createOpenPoll();

        when(pollRepository.findById(pollId)).thenReturn(Optional.of(openPoll));

        // When
        closePollImpl.execute(input);

        // Then
        verify(pollRepository, times(1)).findById(pollId);
        verify(pollRepository, times(1)).update(openPoll);
    }

    @Test
    void shouldReturnClosePollOutputWithCorrectPollOptionIds() {
        // Given
        String pollId = "poll-option-ids";
        ClosePollInput input = new ClosePollInput(pollId);
        Poll openPoll = createOpenPoll();

        when(pollRepository.findById(pollId)).thenReturn(Optional.of(openPoll));

        // When
        ClosePollOutput output = closePollImpl.execute(input);

        // Then
        assertEquals(2, output.options().size());
        assertEquals("option-1", output.options().get(0).pollOptionId());
        assertEquals("Option 1", output.options().get(0).description());
        assertEquals("option-2", output.options().get(1).pollOptionId());
        assertEquals("Option 2", output.options().get(1).description());

        verify(pollRepository, times(1)).findById(pollId);
        verify(pollRepository, times(1)).update(any(Poll.class));
    }
}
