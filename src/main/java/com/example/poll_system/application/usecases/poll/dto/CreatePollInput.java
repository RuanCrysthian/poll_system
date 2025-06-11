package com.example.poll_system.application.usecases.poll.dto;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados necessários para criar uma nova enquete")
public record CreatePollInput(
                @Schema(description = "Título da enquete", example = "Enquete sobre tecnologias preferidas", required = true) String title,
                @Schema(description = "Descrição detalhada da enquete", example = "Esta enquete visa identificar as tecnologias mais populares entre desenvolvedores", required = true) String description,
                @Schema(description = "Data e hora de início da enquete (opcional para enquetes abertas imediatamente)", example = "2025-06-12T10:00:00") LocalDateTime startDate,
                @Schema(description = "Data e hora de término da enquete", example = "2025-06-20T23:59:59", required = true) LocalDateTime endDate,
                @Schema(description = "ID do usuário proprietário da enquete (deve ser ADMIN)", example = "550e8400-e29b-41d4-a716-446655440000", required = true) String ownerId,
                @Schema(description = "Lista de opções da enquete (mínimo 2, máximo 10)", required = true) List<PollOptionInput> options) {

}
