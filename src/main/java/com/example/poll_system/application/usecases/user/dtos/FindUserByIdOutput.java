package com.example.poll_system.application.usecases.user.dtos;

public record FindUserByIdOutput(
        String userId,
        String name,
        String cpf,
        String email,
        String profilePictureUrl,
        String role,
        Boolean isActive) {
}
