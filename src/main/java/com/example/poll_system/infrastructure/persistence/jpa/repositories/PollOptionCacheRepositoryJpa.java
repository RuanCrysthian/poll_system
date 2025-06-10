package com.example.poll_system.infrastructure.persistence.jpa.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.example.poll_system.domain.entities.PollOption;
import com.example.poll_system.domain.gateways.PollOptionRepository;
import com.example.poll_system.infrastructure.persistence.jpa.cache.CacheStore;
import com.example.poll_system.infrastructure.persistence.jpa.entities.PollOptionEntity;
import com.example.poll_system.infrastructure.persistence.jpa.mappers.PollOptionMapper;

@Repository
@Profile("jpa")
public class PollOptionCacheRepositoryJpa implements PollOptionRepository {

    private final PollOptionJpaRepository jpaRepository;
    private final CacheStore<String, PollOptionEntity> cacheStore;
    private final PollOptionMapper pollOptionMapper;

    public PollOptionCacheRepositoryJpa(
            PollOptionJpaRepository jpaRepository,
            CacheStore<String, PollOptionEntity> cacheStore,
            PollOptionMapper pollOptionMapper) {
        this.jpaRepository = jpaRepository;
        this.cacheStore = cacheStore;
        this.pollOptionMapper = pollOptionMapper;
    }

    @Override
    public void save(PollOption pollOption) {
        PollOptionEntity entity = pollOptionMapper.toEntity(pollOption);
        PollOptionEntity savedEntity = jpaRepository.save(entity);
        cacheStore.put("pollOptionId:" + savedEntity.getId(), savedEntity);
    }

    @Override
    public void saveAll(List<PollOption> pollOptions) {
        List<PollOptionEntity> entities = pollOptionMapper.toEntityList(pollOptions);
        jpaRepository.saveAll(entities);
    }

    @Override
    public Optional<PollOption> findById(String id) {
        Optional<PollOptionEntity> cachedEntity = cacheStore.get("pollOptionId:" + id);
        if (cachedEntity.isPresent()) {
            System.out.println("✅ Cache HIT for poll option ID: " + id);
            return Optional.of(pollOptionMapper.toDomain(cachedEntity.get()));
        }
        System.out.println("❌ Cache MISS for poll option ID: " + id);
        Optional<PollOptionEntity> entity = jpaRepository.findById(id);
        if (entity.isPresent()) {
            cacheStore.put("pollOptionId:" + id, entity.get());
            return Optional.of(pollOptionMapper.toDomain(entity.get()));
        }
        return Optional.empty();
    }

    @Override
    public List<PollOption> findByPollId(String pollId) {
        List<PollOptionEntity> entities = jpaRepository.findByPollId(pollId);
        return pollOptionMapper.toDomainList(entities);
    }

}
