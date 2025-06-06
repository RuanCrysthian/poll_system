package com.example.poll_system.application.usecases.user.impl;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final Logger logger = LoggerFactory.getLogger(CreateUserImpl.class);

    public CreateUserOutput execute(CreateUserInput input) {
        validateInput(input);
        User user = buildUser(input);
        userRepository.save(user);
        sendInfoLogMessageUserCreated(user);
        return toOutput(user);
    }

    private void validateInput(CreateUserInput input) {
        validateIfEmployeeExistsByCpf(input.cpf());
        validateIfEmployeeExistsByEmail(input.email());
    }

    private void validateIfEmployeeExistsByCpf(String cpf) {
        if (userRepository.findByCpf(cpf).isPresent()) {
            sendWarningLogMessageUserAlreadyExistsByCpf(cpf);
            throw new BusinessRulesException("User with this CPF already exists");
        }
    }

    private void sendWarningLogMessageUserAlreadyExistsByCpf(String cpf) {
        logger.warn("User creation failed - CPF already exists: {}", cpf);
    }

    private void validateIfEmployeeExistsByEmail(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            sendWarningLogMessageUserAlreadyExistsByEmail(email);
            throw new BusinessRulesException("User with this email already exists");
        }
    }

    private void sendWarningLogMessageUserAlreadyExistsByEmail(String email) {
        logger.warn("User creation failed - Email already exists: {}", email);
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
            logger.error("Failed to upload user profile image - userId: {}, error: {}", input.name(), e.getMessage());
            throw new BusinessRulesException("Error uploading image profile " + e.getMessage());
        }
    }

    private void sendInfoLogMessageUserCreated(User user) {
        logger.info("User created successfully - userId: {}, email: {}, role: {}",
                user.getId(), user.getEmail().getEmail(), user.getRole().name());
    }

    private CreateUserOutput toOutput(User user) {
        return new CreateUserOutput(
                user.getId(),
                user.getName(),
                user.getCpf().getCpf(),
                user.getEmail().getEmail(),
                user.getUrlImageProfile(),
                user.getRole().name());
    }
}
