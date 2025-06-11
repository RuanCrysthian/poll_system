package com.example.poll_system.application.usecases.auth.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados necessários para realizar login no sistema")
public record LoginInput(
        @Schema(description = "Email do usuário", example = "admin@example.com", required = true) String email,
        @Schema(description = "Senha do usuário", example = "password123", required = true) String password) {

    public void validate() {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }
    }
}
