package com.example.poll_system.domain.gateways;

import java.util.List;
import java.util.Optional;

import com.example.poll_system.domain.entities.Vote;

public interface VoteRepository {
    void save(Vote vote);

    Optional<Vote> findById(String id);

    List<Vote> findAll();

    void delete(String id);
}
