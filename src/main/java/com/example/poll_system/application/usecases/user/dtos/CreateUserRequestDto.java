package com.example.poll_system.application.usecases.user.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados JSON para criar um novo usuário (enviado como parte 'user' do multipart)")
public class CreateUserRequestDto {
    @Schema(description = "Nome completo do usuário", example = "João Silva", required = true)
    private String name;

    @Schema(description = "CPF do usuário (apenas números)", example = "12345678901", required = true)
    private String cpf;

    @Schema(description = "Email do usuário", example = "joao.silva@email.com", required = true)
    private String email;

    @Schema(description = "Senha do usuário", example = "MinhaSenh@123", required = true)
    private String password;

    @Schema(description = "Papel do usuário no sistema", example = "ADMIN", allowableValues = { "ADMIN", "VOTER" }, required = true)
    private String role;

    public CreateUserRequestDto() {
    }

    public CreateUserRequestDto(String name, String cpf, String email, String password, String role) {
        this.name = name;
        this.cpf = cpf;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
