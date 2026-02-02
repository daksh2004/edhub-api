package com.edhub.api.service;

import com.edhub.api.dto.request.LoginRequest;
import com.edhub.api.dto.request.RegisterRequest;
import com.edhub.api.dto.response.AuthResponse;
import com.edhub.api.entity.User;
import com.edhub.api.exception.DuplicateEnrollmentException;
import com.edhub.api.exception.UnauthorizedException;
import com.edhub.api.repository.UserRepository;
import com.edhub.api.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    public Map<String, Object> register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEnrollmentException("Email already in use");
        }
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        User saved = userRepository.save(user);

        return Map.of(
                "id", saved.getId(),
                "email", saved.getEmail(),
                "message", "User registered successfully"
        );
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        String token = jwtTokenProvider.generateToken(user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .expiresIn(jwtExpiration / 1000) // seconds
                .build();
    }
}