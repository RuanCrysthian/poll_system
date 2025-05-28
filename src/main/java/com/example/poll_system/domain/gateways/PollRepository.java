package com.example.poll_system.domain.gateways;

import java.util.List;
import java.util.Optional;

import com.example.poll_system.domain.entities.Poll;

public interface PollRepository {
    void save(Poll poll);

    Optional<Poll> findById(String id);

    List<Poll> findAll();

    void update(Poll poll);

    void delete(String id);

}
