package com.example.poll_system.application.usecases.poll.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados de uma opção da enquete")
public record PollOptionOutput(
                @Schema(description = "ID único da opção da enquete", example = "650e8400-e29b-41d4-a716-446655440001") String pollOptionId,
                @Schema(description = "Descrição da opção", example = "Java") String description) {

}
