package com.example.poll_system.application.usecases.poll.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Estatísticas de uma opção específica da enquete")
public record PollOptionStatistics(
                @Schema(description = "ID único da opção da enquete", example = "650e8400-e29b-41d4-a716-446655440001") String pollOptionId,
                @Schema(description = "Descrição da opção", example = "Java") String pollOptionDescription,
                @Schema(description = "Quantidade de votos recebidos por esta opção", example = "45") Long votesCount) {

}
