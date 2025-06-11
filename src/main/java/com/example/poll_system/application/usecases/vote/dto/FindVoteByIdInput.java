package com.example.poll_system.application.usecases.vote.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados necessários para buscar um voto por ID")
public record FindVoteByIdInput(
                @Schema(description = "ID único do voto a ser buscado", example = "750e8400-e29b-41d4-a716-446655440002", required = true) String voteId) {

}
