package com.example.poll_system.infrastructure.persistence.jpa.repositories;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.example.poll_system.domain.entities.Poll;
import com.example.poll_system.domain.enums.PollStatus;
import com.example.poll_system.domain.gateways.PollRepository;
import com.example.poll_system.infrastructure.persistence.jpa.entities.PollEntity;
import com.example.poll_system.infrastructure.persistence.jpa.mappers.PollMapper;

@Repository
@Profile("jpa")
public class PollRepositoryJpa implements PollRepository {

    @Autowired
    private PollJpaRepository jpaRepository;

    @Autowired
    private PollMapper pollMapper;

    @Override
    public void save(Poll poll) {
        PollEntity entity = pollMapper.toEntity(poll);
        jpaRepository.save(entity);
    }

    @Override
    public Optional<Poll> findById(String id) {
        Optional<PollEntity> entity = jpaRepository.findById(id);
        return entity.map(pollMapper::toDomain);
    }

    @Override
    public List<Poll> findAll() {
        List<PollEntity> entities = jpaRepository.findAll();
        return entities.stream()
                .map(pollMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Page<Poll> findAll(Pageable pageable) {
        Page<PollEntity> entitiesPage = jpaRepository.findAll(pageable);
        return entitiesPage.map(pollMapper::toDomain);
    }

    @Override
    public void update(Poll poll) {
        PollEntity entity = pollMapper.toEntity(poll);
        jpaRepository.save(entity);
    }

    @Override
    public void delete(String id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public List<Poll> findByStatus(PollStatus status) {
        List<PollEntity> entities = jpaRepository.findByStatus(status);
        return entities.stream()
                .map(pollMapper::toDomain)
                .collect(Collectors.toList());
    }
}
