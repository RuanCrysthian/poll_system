package com.example.poll_system.infrastructure.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.example.poll_system.domain.entities.Poll;
import com.example.poll_system.domain.enums.PollStatus;
import com.example.poll_system.domain.exceptions.EntityNotFoundException;
import com.example.poll_system.domain.gateways.PollRepository;

@Repository
@Profile("in-memory")
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
    public Page<Poll> findAll(Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), polls.size());

        if (start >= polls.size()) {
            return new PageImpl<>(new ArrayList<>(), pageable, polls.size());
        }

        List<Poll> pageContent = polls.subList(start, end);
        return new PageImpl<>(pageContent, pageable, polls.size());
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

    @Override
    public List<Poll> findByStatus(PollStatus status) {
        return polls.stream()
                .filter(poll -> poll.getStatus() == status)
                .toList();
    }

}
