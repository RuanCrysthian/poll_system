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
    private String urlImageProfile;
    private Boolean isActive;

    private User(
            String id,
            String name,
            Cpf cpf,
            Email email,
            String password,
            String urlImageProfile,
            UserRole role) {
        this.id = id;
        this.name = name;
        this.cpf = cpf;
        this.email = email;
        this.password = password;
        this.role = role;
        this.urlImageProfile = urlImageProfile;
        this.isActive = true;
        validate();
    }

    private void validate() {
        if (id == null || id.trim().isEmpty()) {
            throw new FieldIsRequiredException("id is required");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new FieldIsRequiredException("name is required");
        }
        if (cpf == null) {
            throw new FieldIsRequiredException("cpf is required");
        }
        if (email == null) {
            throw new FieldIsRequiredException("email is required");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new FieldIsRequiredException("password is required");
        }
    }

    public static User createAdmin(
            String id,
            String name,
            Cpf cpf,
            Email email,
            String password,
            String urlImageProfile) {
        return new User(id, name, cpf, email, password, urlImageProfile, UserRole.ADMIN);
    }

    public static User createVoter(
            String id,
            String name,
            Cpf cpf,
            Email email,
            String password,
            String urlImageProfile) {
        return new User(id, name, cpf, email, password, urlImageProfile, UserRole.VOTER);
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

    public Boolean isActive() {
        return isActive;
    }

    public void activate() {
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }

    public String getUrlImageProfile() {
        return urlImageProfile;
    }

    public Boolean isVoter() {
        return UserRole.VOTER.equals(role);
    }

    public Boolean isAdmin() {
        return UserRole.ADMIN.equals(role);
    }

    public void changeName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return;
        }
        this.name = name;
        validate();
    }

    public void changeCpf(Cpf cpf) {
        if (cpf == null) {
            return;
        }
        this.cpf = cpf;
        validate();
    }

    public void changeEmail(Email email) {
        if (email == null) {
            return;
        }
        this.email = email;
        validate();
    }

    public void changeUrlImageProfile(String urlImageProfile) {
        if (urlImageProfile == null || urlImageProfile.trim().isEmpty()) {
            return;
        }
        this.urlImageProfile = urlImageProfile;
        validate();
    }

    public void changeRole(UserRole role) {
        if (role == null) {
            return;
        }
        this.role = role;
        validate();
    }
}
