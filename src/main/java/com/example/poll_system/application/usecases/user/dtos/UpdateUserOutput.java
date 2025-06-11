package com.example.poll_system.application.usecases.user.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados do usuário atualizado com sucesso")
public record UpdateUserOutput(
                @Schema(description = "ID único do usuário", example = "550e8400-e29b-41d4-a716-446655440000") String userId,

                @Schema(description = "Nome completo do usuário", example = "João Silva Atualizado") String name,

                @Schema(description = "CPF do usuário", example = "12345678901") String cpf,

                @Schema(description = "Email do usuário", example = "joao.silva.novo@email.com") String email,

                @Schema(description = "URL da imagem de perfil atualizada", example = "https://storage.example.com/profile/user123-updated.jpg") String profileImageUrl,

                @Schema(description = "Papel do usuário no sistema", example = "VOTER", allowableValues = {
                                "ADMIN", "VOTER" }) String role,

                @Schema(description = "Status de ativação do usuário", example = "true") Boolean isActive){

}
