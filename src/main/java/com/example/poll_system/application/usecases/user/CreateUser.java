package com.example.poll_system.application.usecases.user;

import com.example.poll_system.application.usecases.user.dtos.CreateUserInput;
import com.example.poll_system.application.usecases.user.dtos.CreateUserOutput;

public interface CreateUser {
    CreateUserOutput execute(CreateUserInput input);
}
