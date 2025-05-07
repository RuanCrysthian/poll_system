package com.example.poll_system.domain.factories;

import java.util.UUID;

import com.example.poll_system.domain.entities.User;
import com.example.poll_system.domain.exceptions.BusinessRulesException;
import com.example.poll_system.domain.value_objects.Cpf;
import com.example.poll_system.domain.value_objects.Email;

public class UserFactory {
    public static User create(
            String name,
            String cpf,
            String email,
            String password,
            String role) {
        switch (role.toLowerCase()) {
            case "admin":
                return User.createAdmin(
                        UUID.randomUUID().toString(),
                        name,
                        new Cpf(cpf),
                        new Email(email),
                        password);
            case "voter":
                return User.createVoter(
                        UUID.randomUUID().toString(),
                        name,
                        new Cpf(cpf),
                        new Email(email),
                        password);
            default:
                throw new BusinessRulesException("Invalid role");
        }
    }
}
