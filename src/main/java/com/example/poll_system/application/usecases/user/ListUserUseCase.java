package com.example.poll_system.application.usecases.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.poll_system.application.usecases.user.dtos.ListUserOutput;

public interface ListUserUseCase {
    Page<ListUserOutput> execute(Pageable pageable);
}
