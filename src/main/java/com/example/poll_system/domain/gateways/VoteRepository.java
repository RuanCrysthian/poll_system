package com.example.poll_system.domain.gateways;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.poll_system.domain.entities.Vote;

public interface VoteRepository {
    void save(Vote vote);

    Optional<Vote> findById(String id);

    List<Vote> findAll();

    Page<Vote> findAll(Pageable pageable);

    void delete(String id);

    List<Vote> findByPollId(String pollId);

    Map<String, Long> countVotesByPollIdGroupedByOptionId(String pollId);
}
