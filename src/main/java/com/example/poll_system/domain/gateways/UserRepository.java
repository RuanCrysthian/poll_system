package com.example.poll_system.domain.gateways;

import java.util.List;
import java.util.Optional;

import com.example.poll_system.domain.entities.User;

public interface UserRepository {
    void save(User user);

    Optional<User> findById(String id);

    List<User> findAll();

    void update(User user);

    void delete(String id);

    Optional<User> findByEmail(String email);

    Optional<User> findByCpf(String cpf);
}
