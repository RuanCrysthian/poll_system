package com.example.poll_system.application.usecases.auth.dtos;

public record LoginInput(
        String email,
        String password) {

    public void validate() {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }
    }
}
