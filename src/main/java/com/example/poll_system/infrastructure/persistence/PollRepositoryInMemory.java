package com.example.poll_system.infrastructure.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.example.poll_system.domain.entities.Poll;
import com.example.poll_system.domain.exceptions.EntityNotFoundException;
import com.example.poll_system.domain.gateways.PollRepository;

@Repository
public class PollRepositoryInMemory implements PollRepository {

    private final List<Poll> polls = new ArrayList<>();

    @Override
    public void save(Poll poll) {
        polls.add(poll);
    }

    @Override
    public Optional<Poll> findById(String id) {
        return polls.stream()
                .filter(poll -> poll.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<Poll> findAll() {
        return polls;
    }

    @Override
    public void update(Poll poll) {
        Poll existingPoll = findById(poll.getId())
                .orElseThrow(() -> new EntityNotFoundException("Poll not found"));
        delete(existingPoll.getId());
        polls.add(poll);
    }

    @Override
    public void delete(String id) {
        polls.removeIf(poll -> poll.getId().equals(id));
    }

}
