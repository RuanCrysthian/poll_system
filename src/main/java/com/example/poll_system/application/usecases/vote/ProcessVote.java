package com.example.poll_system.application.usecases.vote;

import com.example.poll_system.application.usecases.vote.dto.ProcessVoteInput;
import com.example.poll_system.application.usecases.vote.dto.ProcessVoteOutput;

public interface ProcessVote {
    ProcessVoteOutput execute(ProcessVoteInput input);
}
