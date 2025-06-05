package com.example.poll_system.infrastructure.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.poll_system.domain.entities.User;
import com.example.poll_system.domain.value_objects.Cpf;
import com.example.poll_system.domain.value_objects.Email;

class JwtTokenGatewayImplTest {

    private JwtTokenGatewayImpl jwtTokenGateway;
    private User testUser;

    @BeforeEach
    void setUp() {
        jwtTokenGateway = new JwtTokenGatewayImpl();

        // Set test properties using reflection
        ReflectionTestUtils.setField(jwtTokenGateway, "secretKey", "myTestSecretKey123456789012345678901234567890");
        ReflectionTestUtils.setField(jwtTokenGateway, "expirationTime", 3600L); // 1 hour

        testUser = User.createVoter(
                "user-123",
                "John Doe",
                new Cpf("74571762097"),
                new Email("john.doe@example.com"),
                "hashedPassword123",
                "http://example.com/profile.jpg");
    }

    @Test
    void shouldGenerateValidJwtToken() {
        // Act
        String token = jwtTokenGateway.generateToken(testUser);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.contains(".")); // JWT format contains dots

        // Token should have 3 parts separated by dots
        String[] tokenParts = token.split("\\.");
        assertEquals(3, tokenParts.length);
    }

    @Test
    void shouldExtractEmailFromToken() {
        // Arrange
        String token = jwtTokenGateway.generateToken(testUser);

        // Act
        String extractedEmail = jwtTokenGateway.extractEmailFromToken(token);

        // Assert
        assertEquals(testUser.getEmail().getEmail(), extractedEmail);
    }

    @Test
    void shouldReturnTrueForValidToken() {
        // Arrange
        String token = jwtTokenGateway.generateToken(testUser);
        String userEmail = testUser.getEmail().getEmail();

        // Act
        boolean isValid = jwtTokenGateway.isTokenValid(token, userEmail);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void shouldReturnFalseForInvalidToken() {
        // Arrange
        String invalidToken = "invalid.jwt.token";
        String userEmail = testUser.getEmail().getEmail();

        // Act
        boolean isValid = jwtTokenGateway.isTokenValid(invalidToken, userEmail);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void shouldReturnFalseForTokenWithWrongEmail() {
        // Arrange
        String token = jwtTokenGateway.generateToken(testUser);
        String wrongEmail = "wrong@example.com";

        // Act
        boolean isValid = jwtTokenGateway.isTokenValid(token, wrongEmail);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void shouldReturnFalseForExpiredToken() {
        // Arrange - Create gateway with very short expiration time
        JwtTokenGatewayImpl shortExpiryGateway = new JwtTokenGatewayImpl();
        ReflectionTestUtils.setField(shortExpiryGateway, "secretKey", "myTestSecretKey123456789012345678901234567890");
        ReflectionTestUtils.setField(shortExpiryGateway, "expirationTime", -1L); // Already expired

        String expiredToken = shortExpiryGateway.generateToken(testUser);

        // Act
        boolean isExpired = jwtTokenGateway.isTokenExpired(expiredToken);

        // Assert
        assertTrue(isExpired);
    }

    @Test
    void shouldReturnFalseForNonExpiredToken() {
        // Arrange
        String token = jwtTokenGateway.generateToken(testUser);

        // Act
        boolean isExpired = jwtTokenGateway.isTokenExpired(token);

        // Assert
        assertFalse(isExpired);
    }

    @Test
    void shouldReturnTrueForInvalidTokenWhenCheckingExpiration() {
        // Arrange
        String invalidToken = "invalid.token.format";

        // Act
        boolean isExpired = jwtTokenGateway.isTokenExpired(invalidToken);

        // Assert
        assertTrue(isExpired); // Invalid tokens should be considered expired
    }

    @Test
    void shouldReturnCorrectExpirationTime() {
        // Act
        Long expirationTime = jwtTokenGateway.getExpirationTime();

        // Assert
        assertEquals(3600L, expirationTime);
    }

    @Test
    void shouldGenerateTokenWithCorrectClaims() {
        // Arrange
        User adminUser = User.createAdmin(
                "admin-456",
                "Admin User",
                new Cpf("74571762097"),
                new Email("admin@example.com"),
                "hashedPassword456",
                "http://example.com/admin.jpg");

        // Act
        String token = jwtTokenGateway.generateToken(adminUser);

        // Assert
        assertNotNull(token);

        // Extract email to verify token was created correctly
        String extractedEmail = jwtTokenGateway.extractEmailFromToken(token);
        assertEquals(adminUser.getEmail().getEmail(), extractedEmail);
    }

    @Test
    void shouldHandleNullTokenGracefully() {
        // Act & Assert
        assertThrows(Exception.class, () -> {
            jwtTokenGateway.extractEmailFromToken(null);
        });

        assertFalse(jwtTokenGateway.isTokenValid(null, "test@example.com"));
        assertTrue(jwtTokenGateway.isTokenExpired(null));
    }

    @Test
    void shouldHandleEmptyTokenGracefully() {
        // Act & Assert
        assertThrows(Exception.class, () -> {
            jwtTokenGateway.extractEmailFromToken("");
        });

        assertFalse(jwtTokenGateway.isTokenValid("", "test@example.com"));
        assertTrue(jwtTokenGateway.isTokenExpired(""));
    }

    @Test
    void shouldGenerateDifferentTokensForDifferentUsers() {
        // Arrange
        User anotherUser = User.createVoter(
                "user-789",
                "Jane Smith",
                new Cpf("12345678909"), // Valid CPF using algorithm
                new Email("jane.smith@example.com"),
                "hashedPassword789",
                "http://example.com/jane.jpg");

        // Act
        String token1 = jwtTokenGateway.generateToken(testUser);
        String token2 = jwtTokenGateway.generateToken(anotherUser);

        // Assert
        assertNotEquals(token1, token2);

        // Verify each token extracts the correct email
        assertEquals(testUser.getEmail().getEmail(), jwtTokenGateway.extractEmailFromToken(token1));
        assertEquals(anotherUser.getEmail().getEmail(), jwtTokenGateway.extractEmailFromToken(token2));
    }

    @Test
    void shouldValidateTokenOnlyForCorrectUser() {
        // Arrange
        String token = jwtTokenGateway.generateToken(testUser);
        String correctEmail = testUser.getEmail().getEmail();
        String incorrectEmail = "other@example.com";

        // Act & Assert
        assertTrue(jwtTokenGateway.isTokenValid(token, correctEmail));
        assertFalse(jwtTokenGateway.isTokenValid(token, incorrectEmail));
    }
}
