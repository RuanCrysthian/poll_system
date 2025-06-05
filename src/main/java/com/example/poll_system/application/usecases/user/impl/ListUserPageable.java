package com.example.poll_system.application.usecases.user.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.poll_system.application.usecases.user.ListUserUseCase;
import com.example.poll_system.application.usecases.user.dtos.ListUserOutput;
import com.example.poll_system.domain.entities.User;
import com.example.poll_system.domain.gateways.UserRepository;

public class ListUserPageable implements ListUserUseCase {

    private final UserRepository userRepository;

    public ListUserPageable(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Page<ListUserOutput> execute(Pageable pageable) {
        Page<User> usersPage = userRepository.findAll(pageable);
        return usersPage.map(user -> new ListUserOutput(
                user.getId(),
                user.getName(),
                user.getCpf().getCpf(),
                user.getEmail().getEmail(),
                user.getUrlImageProfile(),
                user.getRole().name(),
                user.isActive()));
    }

}
