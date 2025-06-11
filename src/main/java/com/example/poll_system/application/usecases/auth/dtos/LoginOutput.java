package com.example.poll_system.application.usecases.auth.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta do login com informações do usuário e token JWT")
public record LoginOutput(
                @Schema(description = "ID único do usuário", example = "550e8400-e29b-41d4-a716-446655440000") String id,
                @Schema(description = "Nome completo do usuário", example = "João Silva") String name,
                @Schema(description = "Email do usuário", example = "admin@example.com") String email,
                @Schema(description = "Papel do usuário no sistema", example = "ADMIN", allowableValues = {
                                "ADMIN", "VOTER" }) String role,
                @Schema(description = "Token JWT para autenticação", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...") String accessToken,
                @Schema(description = "Tipo do token", example = "Bearer") String tokenType,
                @Schema(description = "Tempo de expiração do token em segundos", example = "86400") Long expiresIn){
}
