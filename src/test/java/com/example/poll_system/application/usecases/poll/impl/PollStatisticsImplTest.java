package com.example.poll_system.application.usecases.poll.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.poll_system.application.usecases.poll.dto.PollOptionStatistics;
import com.example.poll_system.application.usecases.poll.dto.PollStatisticsInput;
import com.example.poll_system.application.usecases.poll.dto.PollStatisticsOutput;
import com.example.poll_system.domain.entities.Poll;
import com.example.poll_system.domain.entities.PollOption;
import com.example.poll_system.domain.enums.PollStatus;
import com.example.poll_system.domain.exceptions.EntityNotFoundException;
import com.example.poll_system.domain.gateways.PollRepository;
import com.example.poll_system.domain.gateways.VoteRepository;

public class PollStatisticsImplTest {

    @InjectMocks
    private PollStatisticsImpl pollStatisticsImpl;

    @Mock
    private PollRepository pollRepository;

    @Mock
    private VoteRepository voteRepository;

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
                new PollOption("option-4", "Option B", pollId),
                new PollOption("option-5", "Option C", pollId));

        return Poll.createScheduledPoll(pollId, title, description, ownerId, startDate, endDate, options);
    }

    private Poll createClosedPoll() {
        Poll poll = createOpenPoll();
        poll.close();
        return poll;
    }

    private Map<String, Long> createVoteCountsMap() {
        Map<String, Long> voteCounts = new HashMap<>();
        voteCounts.put("option-1", 5L);
        voteCounts.put("option-2", 3L);
        return voteCounts;
    }

    private Map<String, Long> createEmptyVoteCountsMap() {
        return new HashMap<>();
    }

    private Map<String, Long> createPartialVoteCountsMap() {
        Map<String, Long> voteCounts = new HashMap<>();
        voteCounts.put("option-1", 10L);
        // option-2 has no votes
        return voteCounts;
    }

    @Test
    void shouldGetPollStatisticsSuccessfully() {
        // Given
        String pollId = "poll-123";
        PollStatisticsInput input = new PollStatisticsInput(pollId);
        Poll poll = createOpenPoll();
        Map<String, Long> voteCounts = createVoteCountsMap();

        when(pollRepository.findById(pollId)).thenReturn(Optional.of(poll));
        when(voteRepository.countVotesByPollIdGroupedByOptionId(pollId)).thenReturn(voteCounts);

        // When
        PollStatisticsOutput output = pollStatisticsImpl.getPollStatistics(input);

        // Then
        assertNotNull(output);
        assertEquals(pollId, output.pollId());
        assertEquals("Test Poll", output.pollTitle());
        assertEquals(PollStatus.OPEN.name(), output.pollStatus());
        assertEquals(8L, output.totalVotes()); // 5 + 3
        assertEquals(2, output.pollOptionsStatistics().size());

        // Verify option statistics
        PollOptionStatistics option1Stats = output.pollOptionsStatistics().get(0);
        assertEquals("option-1", option1Stats.pollOptionId());
        assertEquals("Option 1", option1Stats.pollOptionDescription());
        assertEquals(5L, option1Stats.votesCount());

        PollOptionStatistics option2Stats = output.pollOptionsStatistics().get(1);
        assertEquals("option-2", option2Stats.pollOptionId());
        assertEquals("Option 2", option2Stats.pollOptionDescription());
        assertEquals(3L, option2Stats.votesCount());

        verify(pollRepository, times(1)).findById(pollId);
        verify(voteRepository, times(1)).countVotesByPollIdGroupedByOptionId(pollId);
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenPollDoesNotExist() {
        // Given
        String pollId = "nonexistent-poll";
        PollStatisticsInput input = new PollStatisticsInput(pollId);

        when(pollRepository.findById(pollId)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> pollStatisticsImpl.getPollStatistics(input));

        assertEquals("Poll not found with ID: " + pollId, exception.getMessage());
        verify(pollRepository, times(1)).findById(pollId);
        verify(voteRepository, times(0)).countVotesByPollIdGroupedByOptionId(pollId);
    }

    @Test
    void shouldReturnStatisticsWithZeroVotesWhenNoVotesExist() {
        // Given
        String pollId = "poll-123";
        PollStatisticsInput input = new PollStatisticsInput(pollId);
        Poll poll = createOpenPoll();
        Map<String, Long> emptyVoteCounts = createEmptyVoteCountsMap();

        when(pollRepository.findById(pollId)).thenReturn(Optional.of(poll));
        when(voteRepository.countVotesByPollIdGroupedByOptionId(pollId)).thenReturn(emptyVoteCounts);

        // When
        PollStatisticsOutput output = pollStatisticsImpl.getPollStatistics(input);

        // Then
        assertNotNull(output);
        assertEquals(pollId, output.pollId());
        assertEquals("Test Poll", output.pollTitle());
        assertEquals(PollStatus.OPEN.name(), output.pollStatus());
        assertEquals(0L, output.totalVotes());
        assertEquals(2, output.pollOptionsStatistics().size());

        // Verify both options have zero votes
        output.pollOptionsStatistics().forEach(optionStats -> {
            assertEquals(0L, optionStats.votesCount());
        });

        verify(pollRepository, times(1)).findById(pollId);
        verify(voteRepository, times(1)).countVotesByPollIdGroupedByOptionId(pollId);
    }

    @Test
    void shouldReturnStatisticsWithPartialVotes() {
        // Given
        String pollId = "poll-123";
        PollStatisticsInput input = new PollStatisticsInput(pollId);
        Poll poll = createOpenPoll();
        Map<String, Long> partialVoteCounts = createPartialVoteCountsMap();

        when(pollRepository.findById(pollId)).thenReturn(Optional.of(poll));
        when(voteRepository.countVotesByPollIdGroupedByOptionId(pollId)).thenReturn(partialVoteCounts);

        // When
        PollStatisticsOutput output = pollStatisticsImpl.getPollStatistics(input);

        // Then
        assertNotNull(output);
        assertEquals(pollId, output.pollId());
        assertEquals(10L, output.totalVotes());
        assertEquals(2, output.pollOptionsStatistics().size());

        // Verify option-1 has votes and option-2 has zero votes
        PollOptionStatistics option1Stats = output.pollOptionsStatistics().get(0);
        assertEquals("option-1", option1Stats.pollOptionId());
        assertEquals(10L, option1Stats.votesCount());

        PollOptionStatistics option2Stats = output.pollOptionsStatistics().get(1);
        assertEquals("option-2", option2Stats.pollOptionId());
        assertEquals(0L, option2Stats.votesCount());

        verify(pollRepository, times(1)).findById(pollId);
        verify(voteRepository, times(1)).countVotesByPollIdGroupedByOptionId(pollId);
    }

    @Test
    void shouldReturnStatisticsForScheduledPoll() {
        // Given
        String pollId = "poll-456";
        PollStatisticsInput input = new PollStatisticsInput(pollId);
        Poll scheduledPoll = createScheduledPoll();
        Map<String, Long> emptyVoteCounts = createEmptyVoteCountsMap();

        when(pollRepository.findById(pollId)).thenReturn(Optional.of(scheduledPoll));
        when(voteRepository.countVotesByPollIdGroupedByOptionId(pollId)).thenReturn(emptyVoteCounts);

        // When
        PollStatisticsOutput output = pollStatisticsImpl.getPollStatistics(input);

        // Then
        assertNotNull(output);
        assertEquals(pollId, output.pollId());
        assertEquals("Scheduled Poll", output.pollTitle());
        assertEquals(PollStatus.SCHEDULED.name(), output.pollStatus());
        assertEquals(0L, output.totalVotes());
        assertEquals(3, output.pollOptionsStatistics().size());

        verify(pollRepository, times(1)).findById(pollId);
        verify(voteRepository, times(1)).countVotesByPollIdGroupedByOptionId(pollId);
    }

    @Test
    void shouldReturnStatisticsForClosedPoll() {
        // Given
        String pollId = "poll-123";
        PollStatisticsInput input = new PollStatisticsInput(pollId);
        Poll closedPoll = createClosedPoll();
        Map<String, Long> voteCounts = createVoteCountsMap();

        when(pollRepository.findById(pollId)).thenReturn(Optional.of(closedPoll));
        when(voteRepository.countVotesByPollIdGroupedByOptionId(pollId)).thenReturn(voteCounts);

        // When
        PollStatisticsOutput output = pollStatisticsImpl.getPollStatistics(input);

        // Then
        assertNotNull(output);
        assertEquals(pollId, output.pollId());
        assertEquals("Test Poll", output.pollTitle());
        assertEquals(PollStatus.CLOSED.name(), output.pollStatus());
        assertEquals(8L, output.totalVotes());
        assertEquals(2, output.pollOptionsStatistics().size());

        verify(pollRepository, times(1)).findById(pollId);
        verify(voteRepository, times(1)).countVotesByPollIdGroupedByOptionId(pollId);
    }

    @Test
    void shouldReturnStatisticsWithMultipleOptions() {
        // Given
        String pollId = "poll-multi-options";
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

        Map<String, Long> multipleVoteCounts = new HashMap<>();
        multipleVoteCounts.put("option-1", 15L);
        multipleVoteCounts.put("option-2", 8L);
        multipleVoteCounts.put("option-3", 12L);
        multipleVoteCounts.put("option-4", 5L);

        PollStatisticsInput input = new PollStatisticsInput(pollId);

        when(pollRepository.findById(pollId)).thenReturn(Optional.of(pollWithMultipleOptions));
        when(voteRepository.countVotesByPollIdGroupedByOptionId(pollId)).thenReturn(multipleVoteCounts);

        // When
        PollStatisticsOutput output = pollStatisticsImpl.getPollStatistics(input);

        // Then
        assertNotNull(output);
        assertEquals(pollId, output.pollId());
        assertEquals(title, output.pollTitle());
        assertEquals(PollStatus.OPEN.name(), output.pollStatus());
        assertEquals(40L, output.totalVotes()); // 15 + 8 + 12 + 5
        assertEquals(4, output.pollOptionsStatistics().size());

        // Verify all option statistics
        List<PollOptionStatistics> optionStatistics = output.pollOptionsStatistics();
        assertEquals("option-1", optionStatistics.get(0).pollOptionId());
        assertEquals("Option A", optionStatistics.get(0).pollOptionDescription());
        assertEquals(15L, optionStatistics.get(0).votesCount());

        assertEquals("option-2", optionStatistics.get(1).pollOptionId());
        assertEquals("Option B", optionStatistics.get(1).pollOptionDescription());
        assertEquals(8L, optionStatistics.get(1).votesCount());

        assertEquals("option-3", optionStatistics.get(2).pollOptionId());
        assertEquals("Option C", optionStatistics.get(2).pollOptionDescription());
        assertEquals(12L, optionStatistics.get(2).votesCount());

        assertEquals("option-4", optionStatistics.get(3).pollOptionId());
        assertEquals("Option D", optionStatistics.get(3).pollOptionDescription());
        assertEquals(5L, optionStatistics.get(3).votesCount());

        verify(pollRepository, times(1)).findById(pollId);
        verify(voteRepository, times(1)).countVotesByPollIdGroupedByOptionId(pollId);
    }

    @Test
    void shouldReturnCorrectOutputFormat() {
        // Given
        String pollId = "poll-format-test";
        PollStatisticsInput input = new PollStatisticsInput(pollId);
        Poll poll = createOpenPoll();
        Map<String, Long> voteCounts = createVoteCountsMap();

        when(pollRepository.findById(pollId)).thenReturn(Optional.of(poll));
        when(voteRepository.countVotesByPollIdGroupedByOptionId(pollId)).thenReturn(voteCounts);

        // When
        PollStatisticsOutput output = pollStatisticsImpl.getPollStatistics(input);

        // Then
        assertNotNull(output);
        assertNotNull(output.pollId());
        assertNotNull(output.pollTitle());
        assertNotNull(output.pollStatus());
        assertNotNull(output.totalVotes());
        assertNotNull(output.pollOptionsStatistics());

        // Verify option statistics format
        output.pollOptionsStatistics().forEach(optionStats -> {
            assertNotNull(optionStats.pollOptionId());
            assertNotNull(optionStats.pollOptionDescription());
            assertNotNull(optionStats.votesCount());
        });

        verify(pollRepository, times(1)).findById(pollId);
        verify(voteRepository, times(1)).countVotesByPollIdGroupedByOptionId(pollId);
    }

    @Test
    void shouldMaintainPollDataIntegrityInStatistics() {
        // Given
        String pollId = "poll-integrity-test";
        PollStatisticsInput input = new PollStatisticsInput(pollId);
        Poll originalPoll = createOpenPoll();
        String originalTitle = originalPoll.getTitle();
        String originalStatus = originalPoll.getStatus().name();
        int originalOptionsCount = originalPoll.getOptions().size();
        Map<String, Long> voteCounts = createVoteCountsMap();

        when(pollRepository.findById(pollId)).thenReturn(Optional.of(originalPoll));
        when(voteRepository.countVotesByPollIdGroupedByOptionId(pollId)).thenReturn(voteCounts);

        // When
        PollStatisticsOutput output = pollStatisticsImpl.getPollStatistics(input);

        // Then
        assertEquals(originalTitle, output.pollTitle());
        assertEquals(originalStatus, output.pollStatus());
        assertEquals(originalOptionsCount, output.pollOptionsStatistics().size());

        verify(pollRepository, times(1)).findById(pollId);
        verify(voteRepository, times(1)).countVotesByPollIdGroupedByOptionId(pollId);
    }

    @Test
    void shouldVerifyRepositoryInteractions() {
        // Given
        String pollId = "poll-interactions-test";
        PollStatisticsInput input = new PollStatisticsInput(pollId);
        Poll poll = createOpenPoll();
        Map<String, Long> voteCounts = createVoteCountsMap();

        when(pollRepository.findById(pollId)).thenReturn(Optional.of(poll));
        when(voteRepository.countVotesByPollIdGroupedByOptionId(pollId)).thenReturn(voteCounts);

        // When
        pollStatisticsImpl.getPollStatistics(input);

        // Then
        verify(pollRepository, times(1)).findById(pollId);
        verify(voteRepository, times(1)).countVotesByPollIdGroupedByOptionId(pollId);
    }

    @Test
    void shouldCalculateTotalVotesCorrectly() {
        // Given
        String pollId = "poll-votes-calculation";
        PollStatisticsInput input = new PollStatisticsInput(pollId);
        Poll poll = createOpenPoll();

        Map<String, Long> largeVoteCounts = new HashMap<>();
        largeVoteCounts.put("option-1", 1000L);
        largeVoteCounts.put("option-2", 2500L);

        when(pollRepository.findById(pollId)).thenReturn(Optional.of(poll));
        when(voteRepository.countVotesByPollIdGroupedByOptionId(pollId)).thenReturn(largeVoteCounts);

        // When
        PollStatisticsOutput output = pollStatisticsImpl.getPollStatistics(input);

        // Then
        assertEquals(3500L, output.totalVotes()); // 1000 + 2500
        assertEquals(1000L, output.pollOptionsStatistics().get(0).votesCount());
        assertEquals(2500L, output.pollOptionsStatistics().get(1).votesCount());

        verify(pollRepository, times(1)).findById(pollId);
        verify(voteRepository, times(1)).countVotesByPollIdGroupedByOptionId(pollId);
    }

    @Test
    void shouldReturnStatisticsForPollWithNoVoteCountsInMap() {
        // Given
        String pollId = "poll-123";
        PollStatisticsInput input = new PollStatisticsInput(pollId);
        Poll poll = createOpenPoll();

        // Empty vote counts map - no votes for this poll
        Map<String, Long> emptyVoteCounts = new HashMap<>();

        when(pollRepository.findById(pollId)).thenReturn(Optional.of(poll));
        when(voteRepository.countVotesByPollIdGroupedByOptionId(pollId)).thenReturn(emptyVoteCounts);

        // When
        PollStatisticsOutput output = pollStatisticsImpl.getPollStatistics(input);

        // Then
        assertNotNull(output);
        assertEquals(0L, output.totalVotes()); // No votes for this poll
        assertEquals(2, output.pollOptionsStatistics().size());

        // Both options should have zero votes since there are no votes in the map
        output.pollOptionsStatistics().forEach(optionStats -> {
            assertEquals(0L, optionStats.votesCount());
        });

        verify(pollRepository, times(1)).findById(pollId);
        verify(voteRepository, times(1)).countVotesByPollIdGroupedByOptionId(pollId);
    }
}
