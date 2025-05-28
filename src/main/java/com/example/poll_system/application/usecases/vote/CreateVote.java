package com.example.poll_system.application.usecases.vote;

import com.example.poll_system.application.usecases.vote.dto.CreateVoteInput;

public interface CreateVote {

    void execute(CreateVoteInput input);
}
