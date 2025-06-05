package com.example.poll_system.domain.gateways;

import com.example.poll_system.domain.entities.User;

public interface JwtTokenGateway {
    String generateToken(User user);

    String extractEmailFromToken(String token);

    boolean isTokenValid(String token, String email);

    boolean isTokenExpired(String token);

    Long getExpirationTime();
}
