package com.example.poll_system.application.usecases.auth.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.poll_system.application.usecases.auth.dtos.LoginInput;
import com.example.poll_system.application.usecases.auth.dtos.LoginOutput;
import com.example.poll_system.domain.entities.User;
import com.example.poll_system.domain.enums.UserRole;
import com.example.poll_system.domain.exceptions.BusinessRulesException;
import com.example.poll_system.domain.gateways.JwtTokenGateway;
import com.example.poll_system.domain.gateways.UserRepository;
import com.example.poll_system.domain.value_objects.Cpf;
import com.example.poll_system.domain.value_objects.Email;
import com.example.poll_system.infrastructure.services.PasswordEncoder;

class LoginImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenGateway jwtTokenGateway;

    private LoginImpl loginImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        loginImpl = new LoginImpl(userRepository, passwordEncoder, jwtTokenGateway);
    }

    @Test
    void shouldLoginSuccessfullyWithValidCredentials() {
        // Arrange
        String email = "user@example.com";
        String password = "password123";
        String encodedPassword = "encodedPassword123";
        String accessToken = "jwt.token.here";
        Long expirationTime = 86400L;

        LoginInput input = new LoginInput(email, password);

        User user = User.createVoter(
                "1",
                "John Doe",
                new Cpf("74571762097"),
                new Email(email),
                encodedPassword,
                "http://example.com/image.jpg");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);
        when(jwtTokenGateway.generateToken(user)).thenReturn(accessToken);
        when(jwtTokenGateway.getExpirationTime()).thenReturn(expirationTime);

        // Act
        LoginOutput result = loginImpl.execute(input);

        // Assert
        assertNotNull(result);
        assertEquals(user.getId(), result.id());
        assertEquals(user.getName(), result.name());
        assertEquals(user.getEmail().getEmail(), result.email());
        assertEquals(user.getRole().name(), result.role());
        assertEquals(accessToken, result.accessToken());
        assertEquals("Bearer", result.tokenType());
        assertEquals(expirationTime, result.expiresIn());

        verify(userRepository, times(1)).findByEmail(email);
        verify(passwordEncoder, times(1)).matches(password, encodedPassword);
        verify(jwtTokenGateway, times(1)).generateToken(user);
        verify(jwtTokenGateway, times(1)).getExpirationTime();
    }

    @Test
    void shouldThrowExceptionWhenEmailIsNull() {
        // Arrange
        LoginInput input = new LoginInput(null, "password123");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            loginImpl.execute(input);
        });

        assertEquals("Email is required", exception.getMessage());
        verifyNoInteractions(userRepository, passwordEncoder, jwtTokenGateway);
    }

    @Test
    void shouldThrowExceptionWhenEmailIsEmpty() {
        // Arrange
        LoginInput input = new LoginInput("", "password123");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            loginImpl.execute(input);
        });

        assertEquals("Email is required", exception.getMessage());
        verifyNoInteractions(userRepository, passwordEncoder, jwtTokenGateway);
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsNull() {
        // Arrange
        LoginInput input = new LoginInput("user@example.com", null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            loginImpl.execute(input);
        });

        assertEquals("Password is required", exception.getMessage());
        verifyNoInteractions(userRepository, passwordEncoder, jwtTokenGateway);
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsEmpty() {
        // Arrange
        LoginInput input = new LoginInput("user@example.com", "");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            loginImpl.execute(input);
        });

        assertEquals("Password is required", exception.getMessage());
        verifyNoInteractions(userRepository, passwordEncoder, jwtTokenGateway);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        // Arrange
        String email = "nonexistent@example.com";
        String password = "password123";
        LoginInput input = new LoginInput(email, password);

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        BusinessRulesException exception = assertThrows(BusinessRulesException.class, () -> {
            loginImpl.execute(input);
        });

        assertEquals("Invalid email or password", exception.getMessage());
        verify(userRepository, times(1)).findByEmail(email);
        verifyNoInteractions(passwordEncoder, jwtTokenGateway);
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsIncorrect() {
        // Arrange
        String email = "user@example.com";
        String password = "wrongPassword";
        String encodedPassword = "encodedPassword123";
        LoginInput input = new LoginInput(email, password);

        User user = User.createVoter(
                "1",
                "John Doe",
                new Cpf("74571762097"),
                new Email(email),
                encodedPassword,
                "http://example.com/image.jpg");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(false);

        // Act & Assert
        BusinessRulesException exception = assertThrows(BusinessRulesException.class, () -> {
            loginImpl.execute(input);
        });

        assertEquals("Invalid email or password", exception.getMessage());
        verify(userRepository, times(1)).findByEmail(email);
        verify(passwordEncoder, times(1)).matches(password, encodedPassword);
        verifyNoInteractions(jwtTokenGateway);
    }

    @Test
    void shouldThrowExceptionWhenUserIsInactive() {
        // Arrange
        String email = "user@example.com";
        String password = "password123";
        String encodedPassword = "encodedPassword123";
        LoginInput input = new LoginInput(email, password);

        // Create an inactive user by reflection (since we can't directly set isActive
        // to false)
        User user = User.createVoter(
                "1",
                "John Doe",
                new Cpf("74571762097"),
                new Email(email),
                encodedPassword,
                "http://example.com/image.jpg");

        // We need to create an inactive user differently since the factory always
        // creates active users
        // For this test, we'll mock the behavior instead
        User inactiveUser = mock(User.class);
        when(inactiveUser.getEmail()).thenReturn(new Email(email));
        when(inactiveUser.getPassword()).thenReturn(encodedPassword);
        when(inactiveUser.isActive()).thenReturn(false);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(inactiveUser));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);

        // Act & Assert
        BusinessRulesException exception = assertThrows(BusinessRulesException.class, () -> {
            loginImpl.execute(input);
        });

        assertEquals("User account is inactive", exception.getMessage());
        verify(userRepository, times(1)).findByEmail(email);
        verify(passwordEncoder, times(1)).matches(password, encodedPassword);
        verifyNoInteractions(jwtTokenGateway);
    }

    @Test
    void shouldLoginSuccessfullyWithAdminUser() {
        // Arrange
        String email = "admin@example.com";
        String password = "password123";
        String encodedPassword = "encodedPassword123";
        String accessToken = "jwt.token.here";
        Long expirationTime = 86400L;

        LoginInput input = new LoginInput(email, password);

        User user = User.createAdmin(
                "1",
                "Admin User",
                new Cpf("74571762097"),
                new Email(email),
                encodedPassword,
                "http://example.com/admin.jpg");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);
        when(jwtTokenGateway.generateToken(user)).thenReturn(accessToken);
        when(jwtTokenGateway.getExpirationTime()).thenReturn(expirationTime);

        // Act
        LoginOutput result = loginImpl.execute(input);

        // Assert
        assertNotNull(result);
        assertEquals(user.getId(), result.id());
        assertEquals(user.getName(), result.name());
        assertEquals(user.getEmail().getEmail(), result.email());
        assertEquals(UserRole.ADMIN.name(), result.role());
        assertEquals(accessToken, result.accessToken());
        assertEquals("Bearer", result.tokenType());
        assertEquals(expirationTime, result.expiresIn());

        verify(userRepository, times(1)).findByEmail(email);
        verify(passwordEncoder, times(1)).matches(password, encodedPassword);
        verify(jwtTokenGateway, times(1)).generateToken(user);
        verify(jwtTokenGateway, times(1)).getExpirationTime();
    }

    @Test
    void shouldHandleJwtTokenGenerationCorrectly() {
        // Arrange
        String email = "user@example.com";
        String password = "password123";
        String encodedPassword = "encodedPassword123";
        String accessToken = "generated.jwt.token";
        Long expirationTime = 3600L; // 1 hour

        LoginInput input = new LoginInput(email, password);

        User user = User.createVoter(
                "user-id-123",
                "Test User",
                new Cpf("74571762097"),
                new Email(email),
                encodedPassword,
                null // No profile image
        );

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);
        when(jwtTokenGateway.generateToken(user)).thenReturn(accessToken);
        when(jwtTokenGateway.getExpirationTime()).thenReturn(expirationTime);

        // Act
        LoginOutput result = loginImpl.execute(input);

        // Assert
        assertNotNull(result);
        assertEquals(accessToken, result.accessToken());
        assertEquals("Bearer", result.tokenType());
        assertEquals(expirationTime, result.expiresIn());

        // Verify that JWT gateway was called with the correct user
        verify(jwtTokenGateway, times(1)).generateToken(argThat(u -> u.getId().equals("user-id-123") &&
                u.getEmail().getEmail().equals(email) &&
                u.getName().equals("Test User")));
    }
}
