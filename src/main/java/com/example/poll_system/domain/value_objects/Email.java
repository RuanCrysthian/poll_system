package com.example.poll_system.domain.value_objects;

import com.example.poll_system.domain.exceptions.InvalidFieldException;

public class Email {
    private String email;

    public Email(String email) {
        this.email = email;
        validate();
    }

    private void validate() {
        String emailRegex = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        if (email == null || email.trim().isEmpty()) {
            throw new InvalidFieldException("Email is required");
        }
        if (!email.matches(emailRegex)) {
            throw new InvalidFieldException("Invalid email format");
        }
    }

    public String getEmail() {
        return email;
    }
}
