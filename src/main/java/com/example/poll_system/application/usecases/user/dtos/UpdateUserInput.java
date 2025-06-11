package com.example.poll_system.application.usecases.user.dtos;

import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados necessários para atualizar um usuário existente")
public class UpdateUserInput {

    @Schema(description = "ID único do usuário a ser atualizado", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
    private String userId;

    @Schema(description = "Nome completo do usuário", example = "João Silva Atualizado", required = true)
    private String name;

    @Schema(description = "CPF do usuário (apenas números)", example = "12345678901", required = true)
    private String cpf;

    @Schema(description = "Email do usuário", example = "joao.silva.novo@email.com", required = true)
    private String email;

    @Schema(description = "Papel do usuário no sistema", example = "VOTER", allowableValues = { "ADMIN",
            "VOTER" }, required = true)
    private String role;

    @Schema(description = "Nova imagem de perfil do usuário", required = true)
    private MultipartFile imageProfile;

    public UpdateUserInput(String userId, String name, String cpf, String email, String role,
            MultipartFile imageProfile) {
        this.userId = userId;
        this.name = name;
        this.cpf = cpf;
        this.email = email;
        this.role = role;
        this.imageProfile = imageProfile;
    }

    public String userId() {
        return userId;
    }

    public String name() {
        return name;
    }

    public String cpf() {
        return cpf;
    }

    public String email() {
        return email;
    }

    public String role() {
        return role;
    }

    public MultipartFile imageProfile() {
        return imageProfile;
    }

}
