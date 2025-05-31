package com.example.poll_system.application.usecases.poll.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.poll_system.application.usecases.poll.PollStatistics;
import com.example.poll_system.application.usecases.poll.dto.PollOptionStatistics;
import com.example.poll_system.application.usecases.poll.dto.PollStatisticsInput;
import com.example.poll_system.application.usecases.poll.dto.PollStatisticsOutput;
import com.example.poll_system.domain.entities.Poll;
import com.example.poll_system.domain.entities.PollOption;
import com.example.poll_system.domain.exceptions.EntityNotFoundException;
import com.example.poll_system.domain.gateways.PollRepository;
import com.example.poll_system.domain.gateways.VoteRepository;

@Service
public class PollStatisticsImpl implements PollStatistics {

    private final PollRepository pollRepository;
    private final VoteRepository voteRepository;

    public PollStatisticsImpl(PollRepository pollRepository, VoteRepository voteRepository) {
        this.pollRepository = pollRepository;
        this.voteRepository = voteRepository;
    }

    @Override
    public PollStatisticsOutput getPollStatistics(PollStatisticsInput input) {
        Poll poll = findPollById(input.pollId());
        Map<String, Long> voteCountsByOptionId = getVoteCountsByOptionId(input.pollId());
        List<PollOptionStatistics> optionStatistics = buildOptionStatistics(poll, voteCountsByOptionId);
        long totalVotes = calculateTotalVotes(voteCountsByOptionId);
        return toOutput(poll, totalVotes, optionStatistics);
    }

    private Poll findPollById(String pollId) {
        return pollRepository.findById(pollId)
                .orElseThrow(() -> new EntityNotFoundException("Poll not found with ID: " + pollId));
    }

    private Map<String, Long> getVoteCountsByOptionId(String pollId) {
        return voteRepository.countVotesByPollIdGroupedByOptionId(pollId);
    }

    private List<PollOptionStatistics> buildOptionStatistics(Poll poll, Map<String, Long> voteCountsByOptionId) {
        return poll.getOptions().stream()
                .map(option -> createOptionStatistic(option, voteCountsByOptionId))
                .collect(Collectors.toList());
    }

    private PollOptionStatistics createOptionStatistic(PollOption option, Map<String, Long> voteCountsByOptionId) {
        long voteCount = voteCountsByOptionId.getOrDefault(option.getId(), 0L);
        return new PollOptionStatistics(
                option.getId(),
                option.getDescription(),
                voteCount);
    }

    private long calculateTotalVotes(Map<String, Long> voteCountsByOptionId) {
        return voteCountsByOptionId.values().stream()
                .mapToLong(Long::longValue)
                .sum();
    }

    private PollStatisticsOutput toOutput(Poll poll, long totalVotes, List<PollOptionStatistics> optionStatistics) {
        return new PollStatisticsOutput(
                poll.getId(),
                poll.getTitle(),
                poll.getStatus().name(),
                totalVotes,
                optionStatistics);
    }

}
