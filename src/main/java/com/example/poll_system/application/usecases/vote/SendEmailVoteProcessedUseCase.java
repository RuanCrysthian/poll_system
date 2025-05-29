package com.example.poll_system.application.usecases.vote;

import com.example.poll_system.application.usecases.vote.dto.VoteProcessedEmailInput;

public interface SendEmailVoteProcessedUseCase {
    void execute(VoteProcessedEmailInput input);
}
