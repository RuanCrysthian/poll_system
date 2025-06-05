package com.example.poll_system.application.usecases.user.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.example.poll_system.application.usecases.user.dtos.ListUserOutput;
import com.example.poll_system.domain.entities.User;
import com.example.poll_system.domain.factories.UserFactory;
import com.example.poll_system.domain.gateways.UserRepository;

public class ListUserPageableTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ListUserPageable listUserPageable;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnPagedUsersSuccessfully() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<User> users = createUserList();
        Page<User> usersPage = new PageImpl<>(users, pageable, users.size());

        when(userRepository.findAll(pageable)).thenReturn(usersPage);

        // Act
        Page<ListUserOutput> result = listUserPageable.execute(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(2, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertEquals(0, result.getNumber());

        ListUserOutput firstUser = result.getContent().get(0);
        assertEquals("admin-user", firstUser.userId());
        assertEquals("Admin User", firstUser.name());
        assertEquals("05938337089", firstUser.cpf());
        assertEquals("admin@email.com", firstUser.email());
        assertEquals("http://example.com/admin.jpg", firstUser.profilePictureUrl());
        assertEquals("ADMIN", firstUser.role());
        assertEquals(true, firstUser.isActive());

        ListUserOutput secondUser = result.getContent().get(1);
        assertEquals("voter-user", secondUser.userId());
        assertEquals("Voter User", secondUser.name());
        assertEquals("74571762097", secondUser.cpf());
        assertEquals("voter@email.com", secondUser.email());
        assertEquals("http://example.com/voter.jpg", secondUser.profilePictureUrl());
        assertEquals("VOTER", secondUser.role());
        assertEquals(true, secondUser.isActive());

        verify(userRepository, times(1)).findAll(pageable);
    }

    @Test
    void shouldReturnEmptyPageWhenNoUsersExist() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(userRepository.findAll(pageable)).thenReturn(emptyPage);

        // Act
        Page<ListUserOutput> result = listUserPageable.execute(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getContent().size());
        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getTotalPages());

        verify(userRepository, times(1)).findAll(pageable);
    }

    @Test
    void shouldReturnCorrectPageWhenUsingPagination() {
        // Arrange
        Pageable pageable = PageRequest.of(1, 1);
        List<User> users = createUserList();
        Page<User> usersPage = new PageImpl<>(users.subList(1, 2), pageable, users.size());

        when(userRepository.findAll(pageable)).thenReturn(usersPage);

        // Act
        Page<ListUserOutput> result = listUserPageable.execute(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getTotalPages());
        assertEquals(1, result.getNumber());

        ListUserOutput user = result.getContent().get(0);
        assertEquals("voter-user", user.userId());
        assertEquals("Voter User", user.name());
        assertEquals("VOTER", user.role());

        verify(userRepository, times(1)).findAll(pageable);
    }

    @Test
    void shouldMapUserFieldsCorrectly() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        User user = UserFactory.create(
                "Test User",
                "05938337089",
                "test@email.com",
                "password123",
                "admin",
                "http://example.com/test.jpg");

        Page<User> usersPage = new PageImpl<>(List.of(user), pageable, 1);

        when(userRepository.findAll(pageable)).thenReturn(usersPage);

        // Act
        Page<ListUserOutput> result = listUserPageable.execute(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());

        ListUserOutput userOutput = result.getContent().get(0);
        assertEquals(user.getId(), userOutput.userId());
        assertEquals("Test User", userOutput.name());
        assertEquals("05938337089", userOutput.cpf());
        assertEquals("test@email.com", userOutput.email());
        assertEquals("http://example.com/test.jpg", userOutput.profilePictureUrl());
        assertEquals("ADMIN", userOutput.role());
        assertEquals(true, userOutput.isActive());

        verify(userRepository, times(1)).findAll(pageable);
    }

    @Test
    void shouldHandleDifferentUserRoles() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        User adminUser = UserFactory.create(
                "Admin Test",
                "05938337089",
                "admin@test.com",
                "password123",
                "admin",
                "http://example.com/admin-test.jpg");

        User voterUser = UserFactory.create(
                "Voter Test",
                "74571762097",
                "voter@test.com",
                "password123",
                "voter",
                "http://example.com/voter-test.jpg");

        List<User> users = List.of(adminUser, voterUser);
        Page<User> usersPage = new PageImpl<>(users, pageable, users.size());

        when(userRepository.findAll(pageable)).thenReturn(usersPage);

        // Act
        Page<ListUserOutput> result = listUserPageable.execute(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getContent().size());

        ListUserOutput firstUser = result.getContent().get(0);
        assertEquals("ADMIN", firstUser.role());
        assertEquals("Admin Test", firstUser.name());

        ListUserOutput secondUser = result.getContent().get(1);
        assertEquals("VOTER", secondUser.role());
        assertEquals("Voter Test", secondUser.name());

        verify(userRepository, times(1)).findAll(pageable);
    }

    @Test
    void shouldHandleUsersWithNullProfilePicture() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        User userWithoutPicture = UserFactory.create(
                "No Picture User",
                "74571762097",
                "nopic@email.com",
                "password123",
                "voter",
                null);

        Page<User> usersPage = new PageImpl<>(List.of(userWithoutPicture), pageable, 1);

        when(userRepository.findAll(pageable)).thenReturn(usersPage);

        // Act
        Page<ListUserOutput> result = listUserPageable.execute(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());

        ListUserOutput userOutput = result.getContent().get(0);
        assertEquals(null, userOutput.profilePictureUrl());
        assertEquals("No Picture User", userOutput.name());
        assertEquals("VOTER", userOutput.role());

        verify(userRepository, times(1)).findAll(pageable);
    }

    @Test
    void shouldVerifyRepositoryInteraction() {
        // Arrange
        Pageable pageable = PageRequest.of(2, 5);
        Page<User> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(userRepository.findAll(pageable)).thenReturn(emptyPage);

        // Act
        listUserPageable.execute(pageable);

        // Assert
        verify(userRepository, times(1)).findAll(pageable);
    }

    private List<User> createUserList() {
        User adminUser = UserFactory.create(
                "Admin User",
                "05938337089",
                "admin@email.com",
                "password123",
                "admin",
                "http://example.com/admin.jpg");
        // Definir ID específico para teste consistente
        adminUser = User.createAdmin(
                "admin-user",
                adminUser.getName(),
                adminUser.getCpf(),
                adminUser.getEmail(),
                adminUser.getPassword(),
                adminUser.getUrlImageProfile());

        User voterUser = UserFactory.create(
                "Voter User",
                "74571762097",
                "voter@email.com",
                "password123",
                "voter",
                "http://example.com/voter.jpg");
        // Definir ID específico para teste consistente
        voterUser = User.createVoter(
                "voter-user",
                voterUser.getName(),
                voterUser.getCpf(),
                voterUser.getEmail(),
                voterUser.getPassword(),
                voterUser.getUrlImageProfile());

        return List.of(adminUser, voterUser);
    }
}
