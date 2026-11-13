package com.marketplace.auth.service;

import com.marketplace.auth.model.LoginRequest;
import com.marketplace.auth.model.LoginResponse;
import com.marketplace.auth.model.User;
import com.marketplace.auth.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email().toLowerCase())
                .filter(candidate -> passwordEncoder.matches(request.password(), candidate.passwordHash()))
                .orElseThrow(() -> new InvalidCredentialsException());

        return new LoginResponse(user.id(), user.email(), user.displayName(), "Login successful");
    }
}
