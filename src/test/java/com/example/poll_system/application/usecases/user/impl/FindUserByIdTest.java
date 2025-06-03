package com.example.poll_system.application.usecases.user.impl;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.example.poll_system.application.usecases.user.dtos.FindUserByIdInput;
import com.example.poll_system.application.usecases.user.dtos.FindUserByIdOutput;
import com.example.poll_system.domain.entities.User;
import com.example.poll_system.domain.exceptions.EntityNotFoundException;
import com.example.poll_system.domain.factories.UserFactory;
import com.example.poll_system.domain.gateways.UserRepository;

public class FindUserByIdTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FindUserById findUserById;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        String userId = "nonexistent-id";
        FindUserByIdInput input = new FindUserByIdInput(userId);

        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            findUserById.execute(input);
        });
    }

    @Test
    void shouldReturnUserCorrectlyWhenUserExists() {
        User user = UserFactory.create(
                "John Doe",
                "05938337089",
                "john.doe@email.com",
                "encodedPassword123",
                "admin",
                "uploaded-image-url");
        Mockito.when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        FindUserByIdInput input = new FindUserByIdInput(user.getId());
        FindUserByIdOutput output = findUserById.execute(input);
        Assertions.assertNotNull(output);
        Assertions.assertEquals(user.getId(), output.userId());
        Assertions.assertEquals(user.getName(), output.name());
        Assertions.assertEquals(user.getCpf().getCpf(), output.cpf());
        Assertions.assertEquals(user.getEmail().getEmail(), output.email());
        Assertions.assertEquals(user.getUrlImageProfile(), output.profilePictureUrl());
        Assertions.assertEquals(user.getRole().name(), output.role());
        Assertions.assertTrue(output.isActive());
    }
}
