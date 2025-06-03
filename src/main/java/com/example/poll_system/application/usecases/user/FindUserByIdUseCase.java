package com.example.poll_system.application.usecases.user;

import com.example.poll_system.application.usecases.user.dtos.FindUserByIdInput;
import com.example.poll_system.application.usecases.user.dtos.FindUserByIdOutput;

public interface FindUserByIdUseCase {

    FindUserByIdOutput execute(FindUserByIdInput input);

}
