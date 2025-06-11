package com.example.poll_system.application.usecases.poll.dto;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados detalhados da enquete encontrada")
public record FindPollByIdOutput(
                @Schema(description = "ID único da enquete", example = "123e4567-e89b-12d3-a456-426614174000") String pollId,
                @Schema(description = "Título da enquete", example = "Enquete sobre tecnologias preferidas") String title,
                @Schema(description = "Descrição da enquete", example = "Esta enquete visa identificar as tecnologias mais populares entre desenvolvedores") String description,
                @Schema(description = "ID do usuário proprietário da enquete", example = "550e8400-e29b-41d4-a716-446655440000") String ownerId,
                @Schema(description = "Data e hora de início da enquete", example = "2025-06-12T10:00:00") LocalDateTime startDate,
                @Schema(description = "Data e hora de término da enquete", example = "2025-06-20T23:59:59") LocalDateTime endDate,
                @Schema(description = "Status atual da enquete", example = "OPEN", allowableValues = {
                                "SCHEDULED", "OPEN", "CLOSED" }) String status,
                @Schema(description = "Lista de opções da enquete") List<PollOptionOutput> pollOptions){

}
