package com.example.poll_system.application.usecases.user.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados necessários para buscar um usuário por ID")
public record FindUserByIdInput(
                @Schema(description = "ID único do usuário a ser buscado", example = "550e8400-e29b-41d4-a716-446655440000", required = true) String userId) {
}
