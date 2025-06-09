package com.example.poll_system.infrastructure.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.example.poll_system.domain.entities.User;
import com.example.poll_system.domain.gateways.UserRepository;
import com.example.poll_system.domain.value_objects.Cpf;
import com.example.poll_system.domain.value_objects.Email;

@Repository
@Profile("in-memory")
public class UserRepositoryInMemory implements UserRepository {
    private final List<User> users = new ArrayList<>();

    public UserRepositoryInMemory() {
        users.add(User.createAdmin("1", "John Doe", new Cpf("74571762097"), new Email("john.doe@email.com"),
                "QAZ123qaz*", "fake_url"));
    }

    @Override
    public void save(User user) {
        users.add(user);
    }

    @Override
    public Optional<User> findById(String id) {
        return users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<User> findAll() {
        return users;
    }

    @Override
    public void delete(String id) {
        users.removeIf(user -> user.getId().equals(id));
    }

    @Override
    public void update(User user) {
        User existingUser = findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        delete(existingUser.getId());
        users.add(user);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return users.stream()
                .filter(user -> user.getEmail().getEmail().equals(email))
                .findFirst();
    }

    @Override
    public Optional<User> findByCpf(String cpf) {
        return users.stream()
                .filter(user -> user.getCpf().getCpf().equals(cpf))
                .findFirst();
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAll'");
    }

}
