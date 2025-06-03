package com.example.poll_system.application.usecases.poll;

import com.example.poll_system.application.usecases.poll.dto.FindPollByIdInput;
import com.example.poll_system.application.usecases.poll.dto.FindPollByIdOutput;

public interface FindPollByIdUseCase {
    FindPollByIdOutput execute(FindPollByIdInput input);
}
