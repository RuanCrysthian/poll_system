package com.example.poll_system.application.usecases.vote.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados necessários para criar um voto")
public record CreateVoteInput(
                @Schema(description = "ID único do usuário que está votando", example = "550e8400-e29b-41d4-a716-446655440000", required = true) String userId,
                @Schema(description = "ID da opção de enquete escolhida", example = "650e8400-e29b-41d4-a716-446655440001", required = true) String pollOptionId) {

}
