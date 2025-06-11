package com.example.poll_system.infrastructure.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.poll_system.application.usecases.auth.Login;
import com.example.poll_system.application.usecases.auth.dtos.LoginInput;
import com.example.poll_system.application.usecases.auth.dtos.LoginOutput;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "API para autenticação de usuários no sistema de enquetes")
public class AuthController {

    private final Login login;

    public AuthController(Login login) {
        this.login = login;
    }

    @PostMapping("/login")
    @Operation(summary = "Realizar login no sistema", description = "Autentica um usuário no sistema usando email e senha, retornando um token JWT para acesso às funcionalidades protegidas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginOutput.class))),
            @ApiResponse(responseCode = "400", description = "Dados de login inválidos (email ou senha incorretos, ou usuário inativo)"),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<LoginOutput> login(
            @Parameter(description = "Credenciais do usuário para login", required = true) @RequestBody LoginInput input) {
        LoginOutput output = login.execute(input);
        return ResponseEntity.ok(output);
    }
}
