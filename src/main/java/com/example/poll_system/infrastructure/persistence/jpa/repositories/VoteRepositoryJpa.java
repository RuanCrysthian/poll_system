package com.example.poll_system.infrastructure.persistence.jpa.repositories;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.example.poll_system.domain.entities.Vote;
import com.example.poll_system.domain.gateways.VoteRepository;
import com.example.poll_system.infrastructure.persistence.jpa.entities.VoteEntity;
import com.example.poll_system.infrastructure.persistence.jpa.mappers.VoteMapper;

@Repository
@Profile("jpa")
public class VoteRepositoryJpa implements VoteRepository {

    @Autowired
    private VoteJpaRepository jpaRepository;

    @Autowired
    private VoteMapper voteMapper;

    @Override
    public void save(Vote vote) {
        VoteEntity entity = voteMapper.toEntity(vote);
        jpaRepository.save(entity);
    }

    @Override
    public Optional<Vote> findById(String id) {
        Optional<VoteEntity> entity = jpaRepository.findById(id);
        return entity.map(voteMapper::toDomain);
    }

    @Override
    public List<Vote> findAll() {
        List<VoteEntity> entities = jpaRepository.findAll();
        return entities.stream()
                .map(voteMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Page<Vote> findAll(Pageable pageable) {
        Page<VoteEntity> entitiesPage = jpaRepository.findAll(pageable);
        return entitiesPage.map(voteMapper::toDomain);
    }

    @Override
    public void delete(String id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public List<Vote> findByPollId(String pollId) {
        List<VoteEntity> entities = jpaRepository.findByPollId(pollId);
        return entities.stream()
                .map(voteMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Long> countVotesByPollIdGroupedByOptionId(String pollId) {
        List<VoteJpaRepository.VoteCountProjection> projections = jpaRepository
                .countVotesByPollIdGroupedByOptionId(pollId);

        Map<String, Long> result = new HashMap<>();
        for (VoteJpaRepository.VoteCountProjection projection : projections) {
            result.put(projection.getOptionId(), projection.getCount());
        }

        return result;
    }
}
