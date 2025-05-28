package com.example.poll_system.application.usecases.user.dtos;

import org.springframework.web.multipart.MultipartFile;

public class CreateUserInput {
    private String name;
    private String cpf;
    private String email;
    private String password;
    private String role;
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
