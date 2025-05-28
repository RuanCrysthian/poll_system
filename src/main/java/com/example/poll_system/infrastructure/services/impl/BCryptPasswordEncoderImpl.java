package com.example.poll_system.infrastructure.services.impl;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.poll_system.domain.exceptions.BusinessRulesException;
import com.example.poll_system.infrastructure.services.PasswordEncoder;

@Service
public class BCryptPasswordEncoderImpl implements PasswordEncoder {
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public String encode(String rawPassword) {
        if (!isPasswordValid(rawPassword)) {
            throw new BusinessRulesException(
                    "Invalid password. Password must be at least 8 characters long and include at least one uppercase letter, one lowercase letter, one digit, and one special character.");
        }
        return encoder.encode(rawPassword);
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) {
            throw new BusinessRulesException("Password or encoded password cannot be null.");
        }
        return encoder.matches(rawPassword, encodedPassword);
    }
}
