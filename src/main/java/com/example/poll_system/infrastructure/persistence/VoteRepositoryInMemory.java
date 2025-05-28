package com.example.poll_system.infrastructure.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.example.poll_system.domain.entities.Vote;
import com.example.poll_system.domain.gateways.VoteRepository;

@Repository
public class VoteRepositoryInMemory implements VoteRepository {

    private final List<Vote> votes = new ArrayList<>();

    @Override
    public void save(Vote vote) {
        votes.add(vote);
    }

    @Override
    public Optional<Vote> findById(String id) {
        return votes.stream()
                .filter(vote -> vote.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<Vote> findAll() {
        return votes;
    }

    @Override
    public void delete(String id) {
        votes.removeIf(vote -> vote.getId().equals(id));
    }

}
