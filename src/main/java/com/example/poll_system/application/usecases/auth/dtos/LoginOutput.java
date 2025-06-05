package com.example.poll_system.application.usecases.auth.dtos;

public record LoginOutput(
        String id,
        String name,
        String email,
        String role,
        String accessToken,
        String tokenType,
        Long expiresIn) {
}
