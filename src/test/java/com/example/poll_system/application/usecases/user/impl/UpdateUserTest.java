package com.example.poll_system.application.usecases.user.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

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

public class UpdateUserTest {

    @InjectMocks
    private UpdateUser updateUser;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ObjectStorage objectStorage;

    @Mock
    private MultipartFile imageProfile;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private User createUserAdmin() {
        String id = "1";
        String name = "John Doe";
        Cpf cpf = new Cpf("74571762097");
        Email email = new Email("jane.doe@email.com");
        String password = "password123";
        String urlImageProfile = "http://example.com/image.jpg";

        return User.createAdmin(id, name, cpf, email, password, urlImageProfile);
    }

    private User createUserVoter() {
        String id = "2";
        String name = "Jane Smith";
        Cpf cpf = new Cpf("78887276030");
        Email email = new Email("jane.smith@email.com");
        String password = "password456";
        String urlImageProfile = "http://example.com/jane.jpg";

        return User.createVoter(id, name, cpf, email, password, urlImageProfile);
    }

    private UpdateUserInput createValidUpdateUserInput(String userId) {
        return new UpdateUserInput(
                userId,
                "Updated Name",
                "74571762097", // Using valid CPF
                "updated.email@email.com",
                "ADMIN",
                imageProfile);
    }

    @Test
    void shouldUpdateUserSuccessfullyWhenUserExists() throws Exception {
        // Arrange
        User existingUser = createUserAdmin();
        UpdateUserInput input = createValidUpdateUserInput(existingUser.getId());
        String uploadedImageUrl = "http://example.com/updated-image.jpg";
        InputStream mockInputStream = new ByteArrayInputStream("image data".getBytes());

        Mockito.when(userRepository.findById(existingUser.getId()))
                .thenReturn(Optional.of(existingUser));
        Mockito.when(imageProfile.getInputStream()).thenReturn(mockInputStream);
        Mockito.when(objectStorage.upload(Mockito.anyString(), Mockito.any(InputStream.class)))
                .thenReturn(uploadedImageUrl);

        // Act
        UpdateUserOutput output = updateUser.execute(input);

        // Assert
        Assertions.assertNotNull(output);
        Assertions.assertEquals(existingUser.getId(), output.userId());
        Assertions.assertEquals(input.name(), output.name());
        Assertions.assertEquals(input.cpf(), output.cpf());
        Assertions.assertEquals(input.email(), output.email());
        Assertions.assertEquals(uploadedImageUrl, output.profileImageUrl());
        Assertions.assertEquals(input.role(), output.role());
        Assertions.assertTrue(output.isActive());

        Mockito.verify(userRepository, Mockito.times(1)).findById(existingUser.getId());
        Mockito.verify(objectStorage, Mockito.times(1)).upload(Mockito.anyString(), Mockito.any(InputStream.class));
        Mockito.verify(userRepository, Mockito.times(1)).update(existingUser);
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenUserDoesNotExist() throws Exception {
        // Arrange
        String nonExistentUserId = "nonexistent-id";
        UpdateUserInput input = createValidUpdateUserInput(nonExistentUserId);

        Mockito.when(userRepository.findById(nonExistentUserId))
                .thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> updateUser.execute(input));

        Assertions.assertEquals("User not found", exception.getMessage());
        Mockito.verify(userRepository, Mockito.times(1)).findById(nonExistentUserId);
        Mockito.verify(objectStorage, Mockito.never()).upload(Mockito.anyString(), Mockito.any(InputStream.class));
        Mockito.verify(userRepository, Mockito.never()).update(Mockito.any(User.class));
    }

    @Test
    void shouldThrowBusinessRulesExceptionWhenImageUploadFails() throws Exception {
        // Arrange
        User existingUser = createUserAdmin();
        UpdateUserInput input = createValidUpdateUserInput(existingUser.getId());
        InputStream mockInputStream = new ByteArrayInputStream("image data".getBytes());

        Mockito.when(userRepository.findById(existingUser.getId()))
                .thenReturn(Optional.of(existingUser));
        Mockito.when(imageProfile.getInputStream()).thenReturn(mockInputStream);
        Mockito.when(objectStorage.upload(Mockito.anyString(), Mockito.any(InputStream.class)))
                .thenThrow(new RuntimeException("Storage service unavailable"));

        // Act & Assert
        BusinessRulesException exception = Assertions.assertThrows(
                BusinessRulesException.class,
                () -> updateUser.execute(input));

        Assertions.assertTrue(exception.getMessage().contains("Error uploading image profile"));
        Mockito.verify(userRepository, Mockito.times(1)).findById(existingUser.getId());
        Mockito.verify(objectStorage, Mockito.times(1)).upload(Mockito.anyString(), Mockito.any(InputStream.class));
        Mockito.verify(userRepository, Mockito.never()).update(Mockito.any(User.class));
    }

    @Test
    void shouldUpdateVoterUserToAdminRole() throws Exception {
        // Arrange
        User voterUser = createUserVoter();
        UpdateUserInput input = new UpdateUserInput(
                voterUser.getId(),
                voterUser.getName(),
                voterUser.getCpf().getCpf(),
                voterUser.getEmail().getEmail(),
                "ADMIN",
                imageProfile);
        String uploadedImageUrl = "http://example.com/updated-image.jpg";
        InputStream mockInputStream = new ByteArrayInputStream("image data".getBytes());

        Mockito.when(userRepository.findById(voterUser.getId()))
                .thenReturn(Optional.of(voterUser));
        Mockito.when(imageProfile.getInputStream()).thenReturn(mockInputStream);
        Mockito.when(objectStorage.upload(Mockito.anyString(), Mockito.any(InputStream.class)))
                .thenReturn(uploadedImageUrl);

        // Act
        UpdateUserOutput output = updateUser.execute(input);

        // Assert
        Assertions.assertEquals(UserRole.ADMIN.name(), output.role());
        Mockito.verify(userRepository, Mockito.times(1)).update(voterUser);
    }

    @Test
    void shouldUpdateAdminUserToVoterRole() throws Exception {
        // Arrange
        User adminUser = createUserAdmin();
        UpdateUserInput input = new UpdateUserInput(
                adminUser.getId(),
                adminUser.getName(),
                adminUser.getCpf().getCpf(),
                adminUser.getEmail().getEmail(),
                "VOTER",
                imageProfile);
        String uploadedImageUrl = "http://example.com/updated-image.jpg";
        InputStream mockInputStream = new ByteArrayInputStream("image data".getBytes());

        Mockito.when(userRepository.findById(adminUser.getId()))
                .thenReturn(Optional.of(adminUser));
        Mockito.when(imageProfile.getInputStream()).thenReturn(mockInputStream);
        Mockito.when(objectStorage.upload(Mockito.anyString(), Mockito.any(InputStream.class)))
                .thenReturn(uploadedImageUrl);

        // Act
        UpdateUserOutput output = updateUser.execute(input);

        // Assert
        Assertions.assertEquals(UserRole.VOTER.name(), output.role());
        Mockito.verify(userRepository, Mockito.times(1)).update(adminUser);
    }

    @Test
    void shouldUpdateUserWithNewCpf() throws Exception {
        // Arrange
        User existingUser = createUserAdmin();
        String newCpf = "78887276030";
        UpdateUserInput input = new UpdateUserInput(
                existingUser.getId(),
                existingUser.getName(),
                newCpf,
                existingUser.getEmail().getEmail(),
                existingUser.getRole().name(),
                imageProfile);
        String uploadedImageUrl = "http://example.com/updated-image.jpg";
        InputStream mockInputStream = new ByteArrayInputStream("image data".getBytes());

        Mockito.when(userRepository.findById(existingUser.getId()))
                .thenReturn(Optional.of(existingUser));
        Mockito.when(imageProfile.getInputStream()).thenReturn(mockInputStream);
        Mockito.when(objectStorage.upload(Mockito.anyString(), Mockito.any(InputStream.class)))
                .thenReturn(uploadedImageUrl);

        // Act
        UpdateUserOutput output = updateUser.execute(input);

        // Assert
        Assertions.assertEquals(newCpf, output.cpf());
        Mockito.verify(userRepository, Mockito.times(1)).update(existingUser);
    }

    @Test
    void shouldUpdateUserWithNewEmail() throws Exception {
        // Arrange
        User existingUser = createUserAdmin();
        String newEmail = "newemail@example.com";
        UpdateUserInput input = new UpdateUserInput(
                existingUser.getId(),
                existingUser.getName(),
                existingUser.getCpf().getCpf(),
                newEmail,
                existingUser.getRole().name(),
                imageProfile);
        String uploadedImageUrl = "http://example.com/updated-image.jpg";
        InputStream mockInputStream = new ByteArrayInputStream("image data".getBytes());

        Mockito.when(userRepository.findById(existingUser.getId()))
                .thenReturn(Optional.of(existingUser));
        Mockito.when(imageProfile.getInputStream()).thenReturn(mockInputStream);
        Mockito.when(objectStorage.upload(Mockito.anyString(), Mockito.any(InputStream.class)))
                .thenReturn(uploadedImageUrl);

        // Act
        UpdateUserOutput output = updateUser.execute(input);

        // Assert
        Assertions.assertEquals(newEmail, output.email());
        Mockito.verify(userRepository, Mockito.times(1)).update(existingUser);
    }

    @Test
    void shouldUpdateUserWithNewName() throws Exception {
        // Arrange
        User existingUser = createUserAdmin();
        String newName = "New Updated Name";
        UpdateUserInput input = new UpdateUserInput(
                existingUser.getId(),
                newName,
                existingUser.getCpf().getCpf(),
                existingUser.getEmail().getEmail(),
                existingUser.getRole().name(),
                imageProfile);
        String uploadedImageUrl = "http://example.com/updated-image.jpg";
        InputStream mockInputStream = new ByteArrayInputStream("image data".getBytes());

        Mockito.when(userRepository.findById(existingUser.getId()))
                .thenReturn(Optional.of(existingUser));
        Mockito.when(imageProfile.getInputStream()).thenReturn(mockInputStream);
        Mockito.when(objectStorage.upload(Mockito.anyString(), Mockito.any(InputStream.class)))
                .thenReturn(uploadedImageUrl);

        // Act
        UpdateUserOutput output = updateUser.execute(input);

        // Assert
        Assertions.assertEquals(newName, output.name());
        Mockito.verify(userRepository, Mockito.times(1)).update(existingUser);
    }

    @Test
    void shouldGenerateUniqueFileNameForImageUpload() throws Exception {
        // Arrange
        User existingUser = createUserAdmin();
        UpdateUserInput input = createValidUpdateUserInput(existingUser.getId());
        String uploadedImageUrl = "http://example.com/updated-image.jpg";
        InputStream mockInputStream = new ByteArrayInputStream("image data".getBytes());

        Mockito.when(userRepository.findById(existingUser.getId()))
                .thenReturn(Optional.of(existingUser));
        Mockito.when(imageProfile.getInputStream()).thenReturn(mockInputStream);
        Mockito.when(objectStorage.upload(Mockito.anyString(), Mockito.any(InputStream.class)))
                .thenReturn(uploadedImageUrl);

        // Act
        updateUser.execute(input);

        // Assert - Verifica que um nome de arquivo único é gerado (UUID)
        Mockito.verify(objectStorage).upload(
                Mockito.argThat(fileName -> fileName != null && fileName.length() == 36), // UUID tem 36 caracteres
                Mockito.any(InputStream.class));
    }

    @Test
    void shouldUpdateUserWithCorrectImageUrl() throws Exception {
        // Arrange
        User existingUser = createUserAdmin();
        UpdateUserInput input = createValidUpdateUserInput(existingUser.getId());
        String expectedImageUrl = "https://storage.example.com/newimage123.jpg";
        InputStream mockInputStream = new ByteArrayInputStream("image data".getBytes());

        Mockito.when(userRepository.findById(existingUser.getId()))
                .thenReturn(Optional.of(existingUser));
        Mockito.when(imageProfile.getInputStream()).thenReturn(mockInputStream);
        Mockito.when(objectStorage.upload(Mockito.anyString(), Mockito.any(InputStream.class)))
                .thenReturn(expectedImageUrl);

        // Act
        UpdateUserOutput output = updateUser.execute(input);

        // Assert
        Assertions.assertEquals(expectedImageUrl, output.profileImageUrl());
    }

    @Test
    void shouldPreserveUserActiveStatus() throws Exception {
        // Arrange
        User existingUser = createUserAdmin();
        existingUser.deactivate(); // Desativa o usuário para testar se o status é preservado
        UpdateUserInput input = createValidUpdateUserInput(existingUser.getId());
        String uploadedImageUrl = "http://example.com/updated-image.jpg";
        InputStream mockInputStream = new ByteArrayInputStream("image data".getBytes());

        Mockito.when(userRepository.findById(existingUser.getId()))
                .thenReturn(Optional.of(existingUser));
        Mockito.when(imageProfile.getInputStream()).thenReturn(mockInputStream);
        Mockito.when(objectStorage.upload(Mockito.anyString(), Mockito.any(InputStream.class)))
                .thenReturn(uploadedImageUrl);

        // Act
        UpdateUserOutput output = updateUser.execute(input);

        // Assert
        Assertions.assertFalse(output.isActive()); // Deve manter o status inativo
    }

    @Test
    void shouldHandleValidRoleConversion() throws Exception {
        // Arrange
        User existingUser = createUserAdmin();
        UpdateUserInput input = new UpdateUserInput(
                existingUser.getId(),
                existingUser.getName(),
                existingUser.getCpf().getCpf(),
                existingUser.getEmail().getEmail(),
                "voter", // Teste com role em lowercase
                imageProfile);
        String uploadedImageUrl = "http://example.com/updated-image.jpg";
        InputStream mockInputStream = new ByteArrayInputStream("image data".getBytes());

        Mockito.when(userRepository.findById(existingUser.getId()))
                .thenReturn(Optional.of(existingUser));
        Mockito.when(imageProfile.getInputStream()).thenReturn(mockInputStream);
        Mockito.when(objectStorage.upload(Mockito.anyString(), Mockito.any(InputStream.class)))
                .thenReturn(uploadedImageUrl);

        // Act
        UpdateUserOutput output = updateUser.execute(input);

        // Assert
        Assertions.assertEquals(UserRole.VOTER.name(), output.role());
    }

    @Test
    void shouldExecuteAllStepsInCorrectOrder() throws Exception {
        // Arrange
        User existingUser = createUserAdmin();
        UpdateUserInput input = createValidUpdateUserInput(existingUser.getId());
        String uploadedImageUrl = "http://example.com/updated-image.jpg";
        InputStream mockInputStream = new ByteArrayInputStream("image data".getBytes());

        Mockito.when(userRepository.findById(existingUser.getId()))
                .thenReturn(Optional.of(existingUser));
        Mockito.when(imageProfile.getInputStream()).thenReturn(mockInputStream);
        Mockito.when(objectStorage.upload(Mockito.anyString(), Mockito.any(InputStream.class)))
                .thenReturn(uploadedImageUrl);

        // Act
        updateUser.execute(input);

        // Assert - Verifica a ordem de execução
        var inOrder = Mockito.inOrder(userRepository, objectStorage);
        inOrder.verify(userRepository).findById(existingUser.getId());
        inOrder.verify(objectStorage).upload(Mockito.anyString(), Mockito.any(InputStream.class));
        inOrder.verify(userRepository).update(existingUser);
    }
}
