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

import com.example.poll_system.application.usecases.user.dtos.CreateUserInput;
import com.example.poll_system.application.usecases.user.dtos.CreateUserOutput;
import com.example.poll_system.domain.entities.User;
import com.example.poll_system.domain.exceptions.BusinessRulesException;
import com.example.poll_system.domain.factories.UserFactory;
import com.example.poll_system.domain.gateways.UserRepository;
import com.example.poll_system.infrastructure.services.ObjectStorage;
import com.example.poll_system.infrastructure.services.PasswordEncoder;

public class CreateUserImplTest {

    @InjectMocks
    private CreateUserImpl createUserImpl;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ObjectStorage objectStorage;

    @Mock
    private MultipartFile imageProfile;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private CreateUserInput createValidUserInput() {
        return new CreateUserInput(
                "John Doe",
                "05938337089",
                "john.doe@email.com",
                "QAZ123qaz*",
                "admin",
                imageProfile);
    }

    private User createUser() {
        return UserFactory.create(
                "John Doe",
                "05938337089",
                "john.doe@email.com",
                "encodedPassword123",
                "admin",
                "uploaded-image-url");
    }

    @Test
    void shouldCreateUserSuccessfullyWhenInputIsValid() throws Exception {
        // Arrange
        CreateUserInput input = createValidUserInput();
        String encodedPassword = "encodedPassword123";
        String uploadedImageUrl = "uploaded-image-url";
        InputStream mockInputStream = new ByteArrayInputStream("image data".getBytes());

        Mockito.when(userRepository.findByCpf(input.cpf())).thenReturn(Optional.empty());
        Mockito.when(userRepository.findByEmail(input.email())).thenReturn(Optional.empty());
        Mockito.when(passwordEncoder.encode(input.password())).thenReturn(encodedPassword);
        Mockito.when(imageProfile.getInputStream()).thenReturn(mockInputStream);
        Mockito.when(objectStorage.upload(Mockito.anyString(), Mockito.any(InputStream.class)))
                .thenReturn(uploadedImageUrl);

        // Act
        CreateUserOutput output = createUserImpl.execute(input);

        // Assert
        Assertions.assertNotNull(output);
        Assertions.assertEquals(input.name(), output.name());
        Assertions.assertEquals(input.cpf(), output.cpf());
        Assertions.assertEquals(input.email(), output.email());
        Assertions.assertEquals(input.role().toUpperCase(), output.role());
        Assertions.assertEquals(uploadedImageUrl, output.imageProfileUrl());
        Assertions.assertNotNull(output.id());

        Mockito.verify(userRepository, Mockito.times(1)).findByCpf(input.cpf());
        Mockito.verify(userRepository, Mockito.times(1)).findByEmail(input.email());
        Mockito.verify(passwordEncoder, Mockito.times(1)).encode(input.password());
        Mockito.verify(objectStorage, Mockito.times(1)).upload(Mockito.anyString(), Mockito.any(InputStream.class));
        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(User.class));
    }

    @Test
    void shouldThrowBusinessRulesExceptionWhenCpfAlreadyExists() throws Exception {
        // Arrange
        CreateUserInput input = createValidUserInput();
        User existingUser = createUser();

        Mockito.when(userRepository.findByCpf(input.cpf())).thenReturn(Optional.of(existingUser));

        // Act & Assert
        BusinessRulesException exception = Assertions.assertThrows(
                BusinessRulesException.class,
                () -> createUserImpl.execute(input));

        Assertions.assertEquals("User with this CPF already exists", exception.getMessage());
        Mockito.verify(userRepository, Mockito.times(1)).findByCpf(input.cpf());
        Mockito.verify(userRepository, Mockito.never()).findByEmail(Mockito.anyString());
        Mockito.verify(passwordEncoder, Mockito.never()).encode(Mockito.anyString());
        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any(User.class));
    }

    @Test
    void shouldThrowBusinessRulesExceptionWhenEmailAlreadyExists() throws Exception {
        // Arrange
        CreateUserInput input = createValidUserInput();
        User existingUser = createUser();

        Mockito.when(userRepository.findByCpf(input.cpf())).thenReturn(Optional.empty());
        Mockito.when(userRepository.findByEmail(input.email())).thenReturn(Optional.of(existingUser));

        // Act & Assert
        BusinessRulesException exception = Assertions.assertThrows(
                BusinessRulesException.class,
                () -> createUserImpl.execute(input));

        Assertions.assertEquals("User with this email already exists", exception.getMessage());
        Mockito.verify(userRepository, Mockito.times(1)).findByCpf(input.cpf());
        Mockito.verify(userRepository, Mockito.times(1)).findByEmail(input.email());
        Mockito.verify(passwordEncoder, Mockito.never()).encode(Mockito.anyString());
        // No need to verify objectStorage.upload since it's never called in this
        // scenario
        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any(User.class));
    }

    @Test
    void shouldThrowBusinessRulesExceptionWhenImageUploadFails() throws Exception {
        // Arrange
        CreateUserInput input = createValidUserInput();
        String encodedPassword = "encodedPassword123";
        InputStream mockInputStream = new ByteArrayInputStream("image data".getBytes());

        Mockito.when(userRepository.findByCpf(input.cpf())).thenReturn(Optional.empty());
        Mockito.when(userRepository.findByEmail(input.email())).thenReturn(Optional.empty());
        Mockito.when(passwordEncoder.encode(input.password())).thenReturn(encodedPassword);
        Mockito.when(imageProfile.getInputStream()).thenReturn(mockInputStream);
        Mockito.when(objectStorage.upload(Mockito.anyString(), Mockito.any(InputStream.class)))
                .thenThrow(new RuntimeException("Storage service unavailable"));

        // Act & Assert
        BusinessRulesException exception = Assertions.assertThrows(
                BusinessRulesException.class,
                () -> createUserImpl.execute(input));

        Assertions.assertTrue(exception.getMessage().contains("Error uploading image profile"));
        Mockito.verify(userRepository, Mockito.times(1)).findByCpf(input.cpf());
        Mockito.verify(userRepository, Mockito.times(1)).findByEmail(input.email());
        Mockito.verify(passwordEncoder, Mockito.times(1)).encode(input.password());
        Mockito.verify(objectStorage, Mockito.times(1)).upload(Mockito.anyString(), Mockito.any(InputStream.class));
        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any(User.class));
    }

    @Test
    void shouldCreateVoterUserSuccessfully() throws Exception {
        // Arrange
        CreateUserInput input = new CreateUserInput(
                "Jane Doe",
                "74571762097",
                "jane.doe@email.com",
                "QAZ123qaz*",
                "voter",
                imageProfile);
        String encodedPassword = "encodedPassword123";
        String uploadedImageUrl = "uploaded-image-url";
        InputStream mockInputStream = new ByteArrayInputStream("image data".getBytes());

        Mockito.when(userRepository.findByCpf(input.cpf())).thenReturn(Optional.empty());
        Mockito.when(userRepository.findByEmail(input.email())).thenReturn(Optional.empty());
        Mockito.when(passwordEncoder.encode(input.password())).thenReturn(encodedPassword);
        Mockito.when(imageProfile.getInputStream()).thenReturn(mockInputStream);
        Mockito.when(objectStorage.upload(Mockito.anyString(), Mockito.any(InputStream.class)))
                .thenReturn(uploadedImageUrl);

        // Act
        CreateUserOutput output = createUserImpl.execute(input);

        // Assert
        Assertions.assertNotNull(output);
        Assertions.assertEquals("VOTER", output.role());
        Assertions.assertEquals(input.name(), output.name());
        Assertions.assertEquals(input.cpf(), output.cpf());
        Assertions.assertEquals(input.email(), output.email());
    }

    @Test
    void shouldEncodePasswordBeforeCreatingUser() throws Exception {
        // Arrange
        CreateUserInput input = createValidUserInput();
        String rawPassword = input.password();
        String encodedPassword = "encodedPassword123";
        String uploadedImageUrl = "uploaded-image-url";
        InputStream mockInputStream = new ByteArrayInputStream("image data".getBytes());

        Mockito.when(userRepository.findByCpf(input.cpf())).thenReturn(Optional.empty());
        Mockito.when(userRepository.findByEmail(input.email())).thenReturn(Optional.empty());
        Mockito.when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
        Mockito.when(imageProfile.getInputStream()).thenReturn(mockInputStream);
        Mockito.when(objectStorage.upload(Mockito.anyString(), Mockito.any(InputStream.class)))
                .thenReturn(uploadedImageUrl);

        // Act
        createUserImpl.execute(input);

        // Assert
        Mockito.verify(passwordEncoder, Mockito.times(1)).encode(rawPassword);
        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(User.class));
    }

    @Test
    void shouldValidateInputBeforeBuildingUser() throws Exception {
        // Arrange
        CreateUserInput input = createValidUserInput();
        String encodedPassword = "encodedPassword123";
        String uploadedImageUrl = "uploaded-image-url";
        InputStream mockInputStream = new ByteArrayInputStream("image data".getBytes());

        Mockito.when(userRepository.findByCpf(input.cpf())).thenReturn(Optional.empty());
        Mockito.when(userRepository.findByEmail(input.email())).thenReturn(Optional.empty());
        Mockito.when(passwordEncoder.encode(input.password())).thenReturn(encodedPassword);
        Mockito.when(imageProfile.getInputStream()).thenReturn(mockInputStream);
        Mockito.when(objectStorage.upload(Mockito.anyString(), Mockito.any(InputStream.class)))
                .thenReturn(uploadedImageUrl);

        // Act
        createUserImpl.execute(input);

        // Assert - Verifica que as validações foram chamadas antes da criação do
        // usuário
        var inOrder = Mockito.inOrder(userRepository, passwordEncoder, objectStorage);
        inOrder.verify(userRepository).findByCpf(input.cpf());
        inOrder.verify(userRepository).findByEmail(input.email());
        inOrder.verify(passwordEncoder).encode(input.password());
        inOrder.verify(objectStorage).upload(Mockito.anyString(), Mockito.any(InputStream.class));
        inOrder.verify(userRepository).save(Mockito.any(User.class));
    }

    @Test
    void shouldGenerateUniqueFileNameForImageUpload() throws Exception {
        // Arrange
        CreateUserInput input = createValidUserInput();
        String encodedPassword = "encodedPassword123";
        String uploadedImageUrl = "uploaded-image-url";
        InputStream mockInputStream = new ByteArrayInputStream("image data".getBytes());

        Mockito.when(userRepository.findByCpf(input.cpf())).thenReturn(Optional.empty());
        Mockito.when(userRepository.findByEmail(input.email())).thenReturn(Optional.empty());
        Mockito.when(passwordEncoder.encode(input.password())).thenReturn(encodedPassword);
        Mockito.when(imageProfile.getInputStream()).thenReturn(mockInputStream);
        Mockito.when(objectStorage.upload(Mockito.anyString(), Mockito.any(InputStream.class)))
                .thenReturn(uploadedImageUrl);

        // Act
        createUserImpl.execute(input);

        // Assert - Verifica que um nome de arquivo único é gerado (UUID)
        Mockito.verify(objectStorage).upload(Mockito.argThat(fileName -> fileName != null && fileName.length() == 36 // UUID
                                                                                                                     // tem
                                                                                                                     // 36
                                                                                                                     // caracteres
        ), Mockito.any(InputStream.class));
    }

    @Test
    void shouldCreateUserWithCorrectImageUrl() throws Exception {
        // Arrange
        CreateUserInput input = createValidUserInput();
        String encodedPassword = "encodedPassword123";
        String expectedImageUrl = "https://storage.example.com/image123.jpg";
        InputStream mockInputStream = new ByteArrayInputStream("image data".getBytes());

        Mockito.when(userRepository.findByCpf(input.cpf())).thenReturn(Optional.empty());
        Mockito.when(userRepository.findByEmail(input.email())).thenReturn(Optional.empty());
        Mockito.when(passwordEncoder.encode(input.password())).thenReturn(encodedPassword);
        Mockito.when(imageProfile.getInputStream()).thenReturn(mockInputStream);
        Mockito.when(objectStorage.upload(Mockito.anyString(), Mockito.any(InputStream.class)))
                .thenReturn(expectedImageUrl);

        // Act
        CreateUserOutput output = createUserImpl.execute(input);

        // Assert
        Assertions.assertEquals(expectedImageUrl, output.imageProfileUrl());
    }

    @Test
    void shouldNotSaveUserWhenValidationFails() throws Exception {
        // Arrange
        CreateUserInput input = createValidUserInput();
        User existingUser = createUser();

        Mockito.when(userRepository.findByCpf(input.cpf())).thenReturn(Optional.of(existingUser));

        // Act & Assert
        Assertions.assertThrows(
                BusinessRulesException.class,
                () -> createUserImpl.execute(input));

        // Assert - Verifica que o usuário não foi salvo quando a validação falha
        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any(User.class));
    }
}
