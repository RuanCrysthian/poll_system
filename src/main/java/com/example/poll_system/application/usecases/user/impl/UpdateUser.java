package com.example.poll_system.application.usecases.user.impl;

import java.util.UUID;

import com.example.poll_system.application.usecases.user.UpdateUserUseCase;
import com.example.poll_system.application.usecases.user.dtos.UpdateUserInput;
import com.example.poll_system.application.usecases.user.dtos.UpdateUserOutput;
import com.example.poll_system.domain.entities.User;
import com.example.poll_system.domain.enums.UserRole;
import com.example.poll_system.domain.exceptions.BusinessRulesException;
import com.example.poll_system.domain.exceptions.EntityNotFoundException;
import com.example.poll_system.domain.gateways.UserRepository;
import com.example.poll_system.domain.value_objects.Cpf;
import com.example.poll_system.domain.value_objects.Email;
import com.example.poll_system.infrastructure.services.ObjectStorage;

public class UpdateUser implements UpdateUserUseCase {

    private final UserRepository userRepository;
    private final ObjectStorage objectStorageService;

    public UpdateUser(UserRepository userRepository, ObjectStorage objectStorageService) {
        this.userRepository = userRepository;
        this.objectStorageService = objectStorageService;
    }

    @Override
    public UpdateUserOutput execute(UpdateUserInput input) {
        User user = userRepository.findById(input.userId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        user.changeName(input.name());
        user.changeCpf(new Cpf(input.cpf()));
        user.changeEmail(new Email(input.email()));
        user.changeUrlImageProfile(uploadImageProfile(input));
        user.changeRole(UserRole.valueOf(input.role().toUpperCase()));
        userRepository.update(user);
        return new UpdateUserOutput(
                user.getId(),
                user.getName(),
                user.getCpf().getCpf(),
                user.getEmail().getEmail(),
                user.getUrlImageProfile(),
                user.getRole().name(),
                user.isActive());
    }

    private String uploadImageProfile(UpdateUserInput input) {
        try {
            String fileName = UUID.randomUUID().toString();
            return objectStorageService.upload(fileName, input.imageProfile().getInputStream());
        } catch (Exception e) {
            throw new BusinessRulesException("Error uploading image profile " + e.getMessage());
        }
    }

}
