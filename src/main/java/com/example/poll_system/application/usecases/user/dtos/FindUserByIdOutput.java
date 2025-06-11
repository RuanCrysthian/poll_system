package com.example.poll_system.application.usecases.user.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados detalhados do usuário encontrado")
public record FindUserByIdOutput(
                @Schema(description = "ID único do usuário", example = "550e8400-e29b-41d4-a716-446655440000") String userId,

                @Schema(description = "Nome completo do usuário", example = "João Silva") String name,

                @Schema(description = "CPF do usuário", example = "12345678901") String cpf,

                @Schema(description = "Email do usuário", example = "joao.silva@email.com") String email,

                @Schema(description = "URL da imagem de perfil do usuário", example = "https://storage.example.com/profile/user123.jpg") String profilePictureUrl,

                @Schema(description = "Papel do usuário no sistema", example = "ADMIN", allowableValues = {
                                "ADMIN", "VOTER" }) String role,

                @Schema(description = "Status de ativação do usuário", example = "true") Boolean isActive){
}
