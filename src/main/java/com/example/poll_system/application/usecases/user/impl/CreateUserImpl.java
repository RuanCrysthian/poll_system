package com.example.poll_system.application.usecases.user.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.poll_system.application.usecases.user.CreateUser;
import com.example.poll_system.application.usecases.user.dtos.CreateUserInput;
import com.example.poll_system.application.usecases.user.dtos.CreateUserOutput;
import com.example.poll_system.domain.entities.User;
import com.example.poll_system.domain.exceptions.BusinessRulesException;
import com.example.poll_system.domain.factories.UserFactory;
import com.example.poll_system.domain.gateways.UserRepository;
import com.example.poll_system.infrastructure.services.ObjectStorage;
import com.example.poll_system.infrastructure.services.PasswordEncoder;

@Service
public class CreateUserImpl implements CreateUser {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectStorage objectStorageService;

    public CreateUserImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            ObjectStorage objectStorageService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.objectStorageService = objectStorageService;
    }

    public CreateUserOutput execute(CreateUserInput input) {
        validateInput(input);
        User user = buildUser(input);
        userRepository.save(user);
        return buildCreateUserOutput(user);
    }

    private void validateInput(CreateUserInput input) {
        validateIfEmployeeExistsByCpf(input.cpf());
        validateIfEmployeeExistsByEmail(input.email());
    }

    private void validateIfEmployeeExistsByCpf(String cpf) {
        if (userRepository.findByCpf(cpf).isPresent()) {
            throw new BusinessRulesException("User with this CPF already exists");
        }
    }

    private void validateIfEmployeeExistsByEmail(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new BusinessRulesException("User with this email already exists");
        }
    }

    private User buildUser(CreateUserInput input) {
        String passwordEncoded = passwordEncoder.encode(input.password());
        String urlImageProfile = uploadImageProfile(input);
        return UserFactory.create(
                input.name(),
                input.cpf(),
                input.email(),
                passwordEncoded,
                input.role(),
                urlImageProfile);
    }

    private String uploadImageProfile(CreateUserInput input) {
        try {
            String fileName = UUID.randomUUID().toString();
            return objectStorageService.upload(fileName, input.imageProfile().getInputStream());
        } catch (Exception e) {
            throw new BusinessRulesException("Error uploading image profile " + e.getMessage());
        }
    }

    private CreateUserOutput buildCreateUserOutput(User user) {
        return new CreateUserOutput(
                user.getId(),
                user.getName(),
                user.getCpf().getCpf(),
                user.getEmail().getEmail(),
                user.getUrlImageProfile(),
                user.getRole().name());
    }
}
