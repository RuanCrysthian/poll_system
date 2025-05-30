package com.example.poll_system.infrastructure.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.example.poll_system.domain.entities.PollOption;
import com.example.poll_system.domain.gateways.PollOptionRepository;

@Repository
public class PollOptionRepositoryInMemory implements PollOptionRepository {

    private final List<PollOption> pollOptions = new ArrayList<>();

    @Override
    public void save(PollOption pollOption) {
        pollOptions.add(pollOption);
    }

    @Override
    public Optional<PollOption> findById(String id) {
        return pollOptions.stream()
                .filter(option -> option.getId().equals(id))
                .findFirst();
    }

    @Override
    public void saveAll(List<PollOption> pollOptions) {
        for (PollOption option : pollOptions) {
            save(option);
        }
    }

}
