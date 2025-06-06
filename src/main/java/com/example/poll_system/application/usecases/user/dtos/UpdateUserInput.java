package com.example.poll_system.application.usecases.user.dtos;

import org.springframework.web.multipart.MultipartFile;

public class UpdateUserInput {

    private String userId;
    private String name;
    private String cpf;
    private String email;
    private String role;
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
