package com.example.poll_system.application.usecases.poll.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados para criação de uma opção da enquete")
public record PollOptionInput(
                @Schema(description = "Descrição da opção", example = "Java", required = true) String description) {

}
