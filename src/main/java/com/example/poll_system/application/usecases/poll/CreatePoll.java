package com.example.poll_system.application.usecases.poll;

import com.example.poll_system.application.usecases.poll.dto.CreatePollInput;
import com.example.poll_system.application.usecases.poll.dto.CreatePollOutput;

public interface CreatePoll {
    CreatePollOutput execute(CreatePollInput input);
}
