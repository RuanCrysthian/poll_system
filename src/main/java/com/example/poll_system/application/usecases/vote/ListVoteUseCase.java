package com.example.poll_system.application.usecases.vote;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.poll_system.application.usecases.vote.dto.ListVoteOutput;

public interface ListVoteUseCase {
    Page<ListVoteOutput> execute(Pageable pageable);
}
