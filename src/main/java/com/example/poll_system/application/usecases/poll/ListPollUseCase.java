package com.example.poll_system.application.usecases.poll;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.poll_system.application.usecases.poll.dto.ListPollOutput;

public interface ListPollUseCase {
    Page<ListPollOutput> execute(Pageable pageable);
}
