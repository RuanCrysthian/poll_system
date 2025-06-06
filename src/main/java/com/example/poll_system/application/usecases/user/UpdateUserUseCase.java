package com.example.poll_system.application.usecases.user;

import com.example.poll_system.application.usecases.user.dtos.UpdateUserInput;
import com.example.poll_system.application.usecases.user.dtos.UpdateUserOutput;

public interface UpdateUserUseCase {
    UpdateUserOutput execute(UpdateUserInput input);
}
