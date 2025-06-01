package com.example.poll_system.infrastructure.persistence.jpa.repositories;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.example.poll_system.domain.entities.User;
import com.example.poll_system.domain.gateways.UserRepository;
import com.example.poll_system.infrastructure.persistence.jpa.entities.UserEntity;
import com.example.poll_system.infrastructure.persistence.jpa.mappers.UserMapper;

@Repository
@Profile("jpa")
public class UserRepositoryJpa implements UserRepository {

    @Autowired
    private UserJpaRepository jpaRepository;

    @Autowired
    private UserMapper userMapper;

    @Override
    public void save(User user) {
        UserEntity entity = userMapper.toEntity(user);
        jpaRepository.save(entity);
    }

    @Override
    public Optional<User> findById(String id) {
        Optional<UserEntity> entity = jpaRepository.findById(id);
        return entity.map(userMapper::toDomain);
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
        jpaRepository.save(entity);
    }

    @Override
    public void delete(String id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        Optional<UserEntity> entity = jpaRepository.findByEmail(email);
        return entity.map(userMapper::toDomain);
    }

    @Override
    public Optional<User> findByCpf(String cpf) {
        Optional<UserEntity> entity = jpaRepository.findByCpf(cpf);
        return entity.map(userMapper::toDomain);
    }
}
