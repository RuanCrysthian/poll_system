package com.example.poll_system.infrastructure.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.poll_system.application.usecases.auth.Login;
import com.example.poll_system.application.usecases.auth.dtos.LoginInput;
import com.example.poll_system.application.usecases.auth.dtos.LoginOutput;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final Login login;

    public AuthController(Login login) {
        this.login = login;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginOutput> login(@RequestBody LoginInput input) {
        LoginOutput output = login.execute(input);
        return ResponseEntity.ok(output);
    }
}
