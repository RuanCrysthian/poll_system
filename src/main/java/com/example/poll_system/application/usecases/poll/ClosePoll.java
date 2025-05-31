package com.example.poll_system.application.usecases.poll;

import com.example.poll_system.application.usecases.poll.dto.ClosePollInput;
import com.example.poll_system.application.usecases.poll.dto.ClosePollOutput;

public interface ClosePoll {
    ClosePollOutput execute(ClosePollInput input);
}
