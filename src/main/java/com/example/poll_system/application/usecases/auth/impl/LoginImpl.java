package com.example.poll_system.application.usecases.auth.impl;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final Logger logger = LoggerFactory.getLogger(LoginImpl.class);

    @Override
    public LoginOutput execute(LoginInput input) {
        input.validate();

        User user = findUserByEmail(input.email());
        validateUserCredentials(user, input.password());
        validateUserIsActive(user);

        String accessToken = jwtTokenGateway.generateToken(user);
        Long expiresIn = jwtTokenGateway.getExpirationTime();
        sendInfoLogMenssageUserLogin(user);
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
            sendWarningLogMessageUserNotFound(email);
            throw new BusinessRulesException("Invalid email or password");
        }
        return userOptional.get();
    }

    private void validateUserCredentials(User user, String rawPassword) {
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            sendWarningLogMessageInvalidCredentials(user.getEmail().getEmail());
            throw new BusinessRulesException("Invalid email or password");
        }
    }

    private void validateUserIsActive(User user) {
        if (!user.isActive()) {
            sendWarningLogMessageUserInactive(user.getEmail().getEmail());
            throw new BusinessRulesException("User account is inactive");
        }
    }

    private void sendWarningLogMessageUserNotFound(String email) {
        logger.warn("Login attempt failed - user not found for email: {}", email);
    }

    private void sendWarningLogMessageInvalidCredentials(String email) {
        logger.warn("Login attempt failed - invalid credentials for email: {}", email);
    }

    private void sendWarningLogMessageUserInactive(String email) {
        logger.warn("Login attempt failed - inactive user account for email: {}", email);
    }

    private void sendInfoLogMenssageUserLogin(User user) {
        logger.info("User login successfully - userId: {}, email: {}, role: {}",
                user.getId(), user.getEmail().getEmail(), user.getRole().name());
    }
}
