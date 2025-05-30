package com.example.poll_system.application.usecases.poll;

import com.example.poll_system.application.usecases.poll.dto.ActivePollInput;
import com.example.poll_system.application.usecases.poll.dto.ActivePollOutput;

public interface ActivePoll {
    ActivePollOutput execute(ActivePollInput input);
}
