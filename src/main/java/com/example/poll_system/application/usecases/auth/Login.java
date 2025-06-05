package com.example.poll_system.application.usecases.auth;

import com.example.poll_system.application.usecases.auth.dtos.LoginInput;
import com.example.poll_system.application.usecases.auth.dtos.LoginOutput;

public interface Login {
    LoginOutput execute(LoginInput input);
}
