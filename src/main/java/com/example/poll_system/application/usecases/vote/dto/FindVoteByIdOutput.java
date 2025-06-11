package com.example.poll_system.application.usecases.vote.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados detalhados do voto encontrado")
public record FindVoteByIdOutput(
                @Schema(description = "ID único do voto", example = "750e8400-e29b-41d4-a716-446655440002") String voteId,
                @Schema(description = "ID do usuário que votou", example = "550e8400-e29b-41d4-a716-446655440000") String voterId,
                @Schema(description = "ID da enquete", example = "450e8400-e29b-41d4-a716-446655440003") String pollId,
                @Schema(description = "ID da opção escolhida", example = "650e8400-e29b-41d4-a716-446655440001") String pollOptionId,
                @Schema(description = "Data e hora em que o voto foi criado", example = "2024-06-11T10:30:00") LocalDateTime createdAt,
                @Schema(description = "Status do voto", example = "PROCESSED", allowableValues = {
                                "UNPROCESSED", "PROCESSED" }) String voteStatus){

}
