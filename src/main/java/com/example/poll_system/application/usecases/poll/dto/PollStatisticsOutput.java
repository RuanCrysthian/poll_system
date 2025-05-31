package com.example.poll_system.application.usecases.poll.dto;

import java.util.List;

public record PollStatisticsOutput(
        String pollId,
        String pollTitle,
        String pollStatus,
        Long totalVotes,
        List<PollOptionStatistics> pollOptionsStatistics) {

}
