package com.example.poll_system.infrastructure.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.example.poll_system.domain.entities.Vote;
import com.example.poll_system.domain.enums.VoteStatus;
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

    @Override
    public List<Vote> findByPollId(String pollId) {
        return votes.stream()
                .filter(vote -> vote.getPollId().equals(pollId))
                .toList();
    }

    @Override
    public Map<String, Long> countVotesByPollIdGroupedByOptionId(String pollId) {
        return votes.stream()
                .filter(vote -> vote.getPollId().equals(pollId))
                .filter(vote -> vote.getStatus() == VoteStatus.PROCESSED)
                .collect(Collectors.groupingBy(
                        Vote::getPollOptionId,
                        Collectors.counting()));
    }

}
