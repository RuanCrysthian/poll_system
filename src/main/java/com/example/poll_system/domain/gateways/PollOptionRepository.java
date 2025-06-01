package com.example.poll_system.domain.gateways;

import java.util.List;
import java.util.Optional;

import com.example.poll_system.domain.entities.PollOption;

public interface PollOptionRepository {
    void save(PollOption pollOption);

    void saveAll(List<PollOption> pollOptions);

    Optional<PollOption> findById(String id);

    List<PollOption> findByPollId(String pollId);

}
