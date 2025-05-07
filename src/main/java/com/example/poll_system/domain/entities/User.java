package com.example.poll_system.domain.entities;

import com.example.poll_system.domain.enums.UserRole;
import com.example.poll_system.domain.exceptions.FieldIsRequiredException;
import com.example.poll_system.domain.value_objects.Cpf;
import com.example.poll_system.domain.value_objects.Email;

public class User {
    private String id;
    private String name;
    private Cpf cpf;
    private Email email;
    private String password;
    private UserRole role;
    private Boolean isActive;

    private User(
            String id,
            String name,
            Cpf cpf,
            Email email,
            String password,
            UserRole role) {
        this.id = id;
        this.name = name;
        this.cpf = cpf;
        this.email = email;
        this.password = password;
        this.role = role;
        this.isActive = true;
        validate();
    }

    private void validate() {
        if (id == null || id.isEmpty()) {
            throw new FieldIsRequiredException("id is required");
        }
        if (name == null || name.isEmpty()) {
            throw new FieldIsRequiredException("name is required");
        }
        if (cpf == null) {
            throw new FieldIsRequiredException("cpf is required");
        }
        if (email == null) {
            throw new FieldIsRequiredException("email is required");
        }
        if (password == null || password.isEmpty()) {
            throw new FieldIsRequiredException("password is required");
        }
    }

    public static User createAdmin(
            String id,
            String name,
            Cpf cpf,
            Email email,
            String password) {
        return new User(id, name, cpf, email, password, UserRole.ADMIN);
    }

    public static User createVoter(
            String id,
            String name,
            Cpf cpf,
            Email email,
            String password) {
        return new User(id, name, cpf, email, password, UserRole.VOTER);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Cpf getCpf() {
        return cpf;
    }

    public Email getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public UserRole getRole() {
        return role;
    }

    public Boolean getIsActive() {
        return isActive;
    }

}
