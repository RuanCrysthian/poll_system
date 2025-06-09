package com.example.poll_system.infrastructure.config.security;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.poll_system.domain.entities.User;
import com.example.poll_system.domain.gateways.JwtTokenGateway;
import com.example.poll_system.domain.gateways.UserRepository;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenGateway jwtTokenGateway;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtTokenGateway jwtTokenGateway, UserRepository userRepository) {
        this.jwtTokenGateway = jwtTokenGateway;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);

        try {
            userEmail = jwtTokenGateway.extractEmailFromToken(jwt);

            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                Optional<User> userOptional = userRepository.findByEmail(userEmail);

                if (userOptional.isPresent()) {
                    User user = userOptional.get();

                    if (jwtTokenGateway.isTokenValid(jwt, userEmail) && user.isActive()) {
                        List<SimpleGrantedAuthority> authorities = List.of(
                                new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));

                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                user.getEmail().getEmail(),
                                null,
                                authorities);

                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            }
        } catch (ExpiredJwtException ex) {
            // Token expirado - não autentica o usuário, mas permite que a requisição
            // continue
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("""
                    {
                        "status": 401,
                        "error": "Token Expired",
                        "message": "JWT token has expired. Please login again.",
                        "timestamp": "%s",
                        "path": "%s"
                    }
                    """.formatted(java.time.LocalDateTime.now(), request.getRequestURI()));
            return;
        } catch (JwtException ex) {
            // Token inválido - não autentica o usuário, mas permite que a requisição
            // continue
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("""
                    {
                        "status": 401,
                        "error": "Invalid Token",
                        "message": "JWT token is invalid. Please login again.",
                        "timestamp": "%s",
                        "path": "%s"
                    }
                    """.formatted(java.time.LocalDateTime.now(), request.getRequestURI()));
            return;
        } catch (Exception ex) {
            System.err.println("JWT processing error: " + ex.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
