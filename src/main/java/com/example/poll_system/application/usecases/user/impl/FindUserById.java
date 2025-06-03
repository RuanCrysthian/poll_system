package com.example.poll_system.application.usecases.user.impl;

import com.example.poll_system.application.usecases.user.FindUserByIdUseCase;
import com.example.poll_system.application.usecases.user.dtos.FindUserByIdInput;
import com.example.poll_system.application.usecases.user.dtos.FindUserByIdOutput;
import com.example.poll_system.domain.entities.User;
import com.example.poll_system.domain.exceptions.EntityNotFoundException;
import com.example.poll_system.domain.gateways.UserRepository;

public class FindUserById implements FindUserByIdUseCase {

    private final UserRepository userRepository;

    public FindUserById(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public FindUserByIdOutput execute(FindUserByIdInput input) {
        User user = userRepository.findById(input.userId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return new FindUserByIdOutput(
                user.getId(),
                user.getName(),
                user.getCpf().getCpf(),
                user.getEmail().getEmail(),
                user.getUrlImageProfile(),
                user.getRole().name(),
                user.isActive());
    }

}
