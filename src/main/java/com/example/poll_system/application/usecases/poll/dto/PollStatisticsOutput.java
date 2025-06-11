package com.example.poll_system.application.usecases.poll.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Estatísticas completas da enquete incluindo contagem de votos")
public record PollStatisticsOutput(
                @Schema(description = "ID único da enquete", example = "123e4567-e89b-12d3-a456-426614174000") String pollId,
                @Schema(description = "Título da enquete", example = "Enquete sobre tecnologias preferidas") String pollTitle,
                @Schema(description = "Status atual da enquete", example = "OPEN", allowableValues = {
                                "SCHEDULED", "OPEN", "CLOSED" }) String pollStatus,
                @Schema(description = "Número total de votos na enquete", example = "150") Long totalVotes,
                @Schema(description = "Estatísticas detalhadas por opção da enquete") List<PollOptionStatistics> pollOptionsStatistics){

}
