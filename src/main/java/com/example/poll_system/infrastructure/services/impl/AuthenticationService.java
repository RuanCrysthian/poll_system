package com.example.poll_system.infrastructure.services.impl;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.poll_system.domain.entities.User;
import com.example.poll_system.domain.exceptions.BusinessRulesException;
import com.example.poll_system.domain.gateways.UserRepository;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;

    public AuthenticationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessRulesException("User not authenticated");
        }

        String email = authentication.getName();
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            throw new BusinessRulesException("Authenticated user not found");
        }

        return userOptional.get();
    }

    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessRulesException("User not authenticated");
        }

        return authentication.getName();
    }

    public boolean isCurrentUserAdmin() {
        try {
            User currentUser = getCurrentUser();
            return currentUser.isAdmin();
        } catch (BusinessRulesException e) {
            return false;
        }
    }

    public boolean isCurrentUserVoter() {
        try {
            User currentUser = getCurrentUser();
            return currentUser.isVoter();
        } catch (BusinessRulesException e) {
            return false;
        }
    }
}
