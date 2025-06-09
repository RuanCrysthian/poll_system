package com.example.poll_system.infrastructure.persistence.jpa.repositories;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.example.poll_system.domain.entities.User;
import com.example.poll_system.domain.gateways.UserRepository;
import com.example.poll_system.infrastructure.persistence.jpa.cache.CacheStore;
import com.example.poll_system.infrastructure.persistence.jpa.entities.UserEntity;
import com.example.poll_system.infrastructure.persistence.jpa.mappers.UserMapper;

@Repository
@Profile("jpa")
public class UserCacheRepositoryJpa implements UserRepository {

    private final UserJpaRepository jpaRepository;

    private final UserMapper userMapper;

    private final CacheStore<String, UserEntity> cacheStore;

    public UserCacheRepositoryJpa(
            UserJpaRepository jpaRepository,
            UserMapper userMapper,
            CacheStore<String, UserEntity> cacheStore) {
        this.jpaRepository = jpaRepository;
        this.userMapper = userMapper;
        this.cacheStore = cacheStore;
    }

    @Override
    public void save(User user) {
        UserEntity entity = userMapper.toEntity(user);
        UserEntity savedEntity = jpaRepository.save(entity);

        // Cache a entidade salva
        cacheStore.put(savedEntity.getId(), savedEntity);

        // Também cache por email e CPF se necessário
        cacheStore.put("email:" + savedEntity.getEmail(), savedEntity);
        cacheStore.put("cpf:" + savedEntity.getCpf(), savedEntity);
    }

    @Override
    public Optional<User> findById(String id) {
        // 1. Tentar buscar no cache
        Optional<UserEntity> cachedEntity = cacheStore.get(id);
        if (cachedEntity.isPresent()) {
            System.out.println("✅ Cache HIT for user ID: " + id);
            return Optional.of(userMapper.toDomain(cachedEntity.get()));
        }

        // 2. Se não encontrar, buscar no banco
        System.out.println("❌ Cache MISS for user ID: " + id);
        Optional<UserEntity> entity = jpaRepository.findById(id);
        if (entity.isPresent()) {
            UserEntity foundEntity = entity.get();

            // 3. Salvar no cache
            cacheStore.put(id, foundEntity);
            System.out.println("💾 User cached with ID: " + id);

            return Optional.of(userMapper.toDomain(foundEntity));
        }
        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        List<UserEntity> entities = jpaRepository.findAll();
        return entities.stream()
                .map(userMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void update(User user) {
        UserEntity entity = userMapper.toEntity(user);

        // Invalidar caches relacionados
        cacheStore.evict(user.getId());
        cacheStore.evict("email:" + user.getEmail().getEmail());
        cacheStore.evict("cpf:" + user.getCpf().getCpf());

        // Salvar no banco
        UserEntity updatedEntity = jpaRepository.save(entity);

        // Re-cachear com dados atualizados
        cacheStore.put(updatedEntity.getId(), updatedEntity);
        cacheStore.put("email:" + updatedEntity.getEmail(), updatedEntity);
        cacheStore.put("cpf:" + updatedEntity.getCpf(), updatedEntity);
    }

    @Override
    public void delete(String id) {
        // Buscar entity para invalidar todos os caches relacionados
        Optional<UserEntity> entity = jpaRepository.findById(id);
        if (entity.isPresent()) {
            UserEntity userEntity = entity.get();
            cacheStore.evict(id);
            cacheStore.evict("email:" + userEntity.getEmail());
            cacheStore.evict("cpf:" + userEntity.getCpf());
        }

        jpaRepository.deleteById(id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String cacheKey = "email:" + email;

        // 1. Tentar buscar no cache
        Optional<UserEntity> cachedEntity = cacheStore.get(cacheKey);
        if (cachedEntity.isPresent()) {
            System.out.println("✅ Cache HIT for email: " + email);
            return Optional.of(userMapper.toDomain(cachedEntity.get()));
        }

        // 2. Se não encontrar, buscar no banco
        System.out.println("❌ Cache MISS for email: " + email);
        Optional<UserEntity> entity = jpaRepository.findByEmail(email);
        if (entity.isPresent()) {
            UserEntity foundEntity = entity.get();

            // 3. Cache por ID e por email
            cacheStore.put(foundEntity.getId(), foundEntity);
            cacheStore.put(cacheKey, foundEntity);
            System.out.println("💾 User cached by email: " + email);

            return Optional.of(userMapper.toDomain(foundEntity));
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByCpf(String cpf) {
        String cacheKey = "cpf:" + cpf;

        // 1. Tentar buscar no cache
        Optional<UserEntity> cachedEntity = cacheStore.get(cacheKey);
        if (cachedEntity.isPresent()) {
            System.out.println("✅ Cache HIT for CPF: " + cpf);
            return Optional.of(userMapper.toDomain(cachedEntity.get()));
        }

        // 2. Se não encontrar, buscar no banco
        System.out.println("❌ Cache MISS for CPF: " + cpf);
        Optional<UserEntity> entity = jpaRepository.findByCpf(cpf);
        if (entity.isPresent()) {
            UserEntity foundEntity = entity.get();

            // 3. Cache por ID e por CPF
            cacheStore.put(foundEntity.getId(), foundEntity);
            cacheStore.put(cacheKey, foundEntity);
            System.out.println("💾 User cached by CPF: " + cpf);

            return Optional.of(userMapper.toDomain(foundEntity));
        }
        return Optional.empty();
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        // Paginação geralmente não é cacheada por ser muito específica
        Page<UserEntity> entitiesPage = jpaRepository.findAll(pageable);
        return entitiesPage.map(userMapper::toDomain);
    }

}
