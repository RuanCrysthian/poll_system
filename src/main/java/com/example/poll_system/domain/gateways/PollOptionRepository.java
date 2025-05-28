package com.example.poll_system.domain.gateways;

import java.util.Optional;

import com.example.poll_system.domain.entities.PollOption;

public interface PollOptionRepository {
    void save(PollOption pollOption);

    Optional<PollOption> findById(String id);
}
