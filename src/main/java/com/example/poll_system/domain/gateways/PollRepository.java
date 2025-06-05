package com.example.poll_system.domain.gateways;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.poll_system.domain.entities.Poll;
import com.example.poll_system.domain.enums.PollStatus;

public interface PollRepository {
    void save(Poll poll);

    Optional<Poll> findById(String id);

    List<Poll> findAll();

    Page<Poll> findAll(Pageable pageable);

    void update(Poll poll);

    void delete(String id);

    List<Poll> findByStatus(PollStatus status);

}
