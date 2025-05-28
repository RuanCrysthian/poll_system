package com.example.poll_system.application.usecases.user.dtos;

public record CreateUserOutput(
        String id,
        String name,
        String cpf,
        String email,
        String imageProfileUrl,
        String role) {

}
