package com.example.poll_system.application.usecases.user;

import com.example.poll_system.application.usecases.vote.dto.SendEmailInput;

public interface SendEmailUseCase {
    void execute(SendEmailInput input);
}
