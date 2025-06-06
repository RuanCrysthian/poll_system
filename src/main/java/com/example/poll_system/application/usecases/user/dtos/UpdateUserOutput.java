package com.example.poll_system.application.usecases.user.dtos;

public record UpdateUserOutput(
        String userId,
        String name,
        String cpf,
        String email,
        String profileImageUrl,
        String role,
        Boolean isActive) {

}
