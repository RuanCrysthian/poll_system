package com.example.poll_system.infrastructure.persistence.jpa.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.example.poll_system.domain.entities.PollOption;
import com.example.poll_system.domain.gateways.PollOptionRepository;
import com.example.poll_system.infrastructure.persistence.jpa.entities.PollOptionEntity;
import com.example.poll_system.infrastructure.persistence.jpa.mappers.PollOptionMapper;

@Repository
@Profile("jpa")
public class PollOptionRepositoryJpa implements PollOptionRepository {

    @Autowired
    private PollOptionJpaRepository jpaRepository;

    @Autowired
    private PollOptionMapper pollOptionMapper;

    @Override
    public void save(PollOption pollOption) {
        PollOptionEntity entity = pollOptionMapper.toEntity(pollOption);
        jpaRepository.save(entity);
    }

    @Override
    public void saveAll(List<PollOption> pollOptions) {
        List<PollOptionEntity> entities = pollOptionMapper.toEntityList(pollOptions);
        jpaRepository.saveAll(entities);
    }

    @Override
    public Optional<PollOption> findById(String id) {
        Optional<PollOptionEntity> entity = jpaRepository.findById(id);
        return entity.map(pollOptionMapper::toDomain);
    }

    @Override
    public List<PollOption> findByPollId(String pollId) {
        List<PollOptionEntity> entities = jpaRepository.findByPollId(pollId);
        return pollOptionMapper.toDomainList(entities);
    }
}
