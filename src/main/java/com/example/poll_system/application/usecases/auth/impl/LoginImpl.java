package com.example.poll_system.application.usecases.auth.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.poll_system.application.usecases.auth.Login;
import com.example.poll_system.application.usecases.auth.dtos.LoginInput;
import com.example.poll_system.application.usecases.auth.dtos.LoginOutput;
import com.example.poll_system.domain.entities.User;
import com.example.poll_system.domain.exceptions.BusinessRulesException;
import com.example.poll_system.domain.gateways.JwtTokenGateway;
import com.example.poll_system.domain.gateways.UserRepository;
import com.example.poll_system.infrastructure.services.PasswordEncoder;

@Service
public class LoginImpl implements Login {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenGateway jwtTokenGateway;

    public LoginImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenGateway jwtTokenGateway) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenGateway = jwtTokenGateway;
    }

    @Override
    public LoginOutput execute(LoginInput input) {
        input.validate();

        User user = findUserByEmail(input.email());
        validateUserCredentials(user, input.password());
        validateUserIsActive(user);

        String accessToken = jwtTokenGateway.generateToken(user);
        Long expiresIn = jwtTokenGateway.getExpirationTime();

        return new LoginOutput(
                user.getId(),
                user.getName(),
                user.getEmail().getEmail(),
                user.getRole().name(),
                accessToken,
                "Bearer",
                expiresIn);
    }

    private User findUserByEmail(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new BusinessRulesException("Invalid email or password");
        }
        return userOptional.get();
    }

    private void validateUserCredentials(User user, String rawPassword) {
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new BusinessRulesException("Invalid email or password");
        }
    }

    private void validateUserIsActive(User user) {
        if (!user.isActive()) {
            throw new BusinessRulesException("User account is inactive");
        }
    }
}
