package com.example.poll_system.application.usecases.vote;

import com.example.poll_system.application.usecases.vote.dto.FindVoteByIdInput;
import com.example.poll_system.application.usecases.vote.dto.FindVoteByIdOutput;

public interface FindVoteByIdUseCase {
    FindVoteByIdOutput execute(FindVoteByIdInput input);
}
