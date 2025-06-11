package com.example.poll_system.application.usecases.poll.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados necessários para buscar uma enquete por ID")
public record FindPollByIdInput(
                @Schema(description = "ID único da enquete a ser buscada", example = "123e4567-e89b-12d3-a456-426614174000", required = true) String pollId) {

}
