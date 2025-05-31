package com.example.poll_system.application.usecases.poll;

import com.example.poll_system.application.usecases.poll.dto.PollStatisticsInput;
import com.example.poll_system.application.usecases.poll.dto.PollStatisticsOutput;

public interface PollStatistics {
    PollStatisticsOutput getPollStatistics(PollStatisticsInput input);
}
