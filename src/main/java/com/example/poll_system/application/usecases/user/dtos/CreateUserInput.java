package com.example.poll_system.application.usecases.user.dtos;

import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados necessários para criar um novo usuário")
public class CreateUserInput {
    @Schema(description = "Nome completo do usuário", example = "João Silva", required = true)
    private String name;

    @Schema(description = "CPF do usuário (apenas números)", example = "12345678901", required = true)
    private String cpf;

    @Schema(description = "Email do usuário", example = "joao.silva@email.com", required = true)
    private String email;

    @Schema(description = "Senha do usuário", example = "MinhaSenh@123", required = true)
    private String password;

    @Schema(description = "Papel do usuário no sistema", example = "ADMIN", allowableValues = { "ADMIN",
            "VOTER" }, required = true)
    private String role;

    @Schema(hidden = true)
    private MultipartFile imageProfile;

    public CreateUserInput(String name, String cpf, String email, String password, String role,
            MultipartFile imageProfile) {
        this.name = name;
        this.cpf = cpf;
        this.email = email;
        this.password = password;
        this.role = role;
        this.imageProfile = imageProfile;
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

    public String password() {
        return password;
    }

    public String role() {
        return role;
    }

    public MultipartFile imageProfile() {
        return imageProfile;
    }

    public void setImageProfile(MultipartFile imageProfile) {
        this.imageProfile = imageProfile;
    }
}
