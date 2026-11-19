package com.marketplace.auth.service;

import com.marketplace.auth.model.LoginRequest;
import com.marketplace.auth.model.LoginResponse;
import com.marketplace.auth.model.RefreshRequest;
import com.marketplace.auth.model.RegisterRequest;
import com.marketplace.auth.model.LogoutResponse;
import com.marketplace.auth.model.LogoutRequest;
import com.marketplace.auth.model.UserResponse;
import com.marketplace.auth.model.User;
import com.marketplace.auth.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RevokedTokenService revokedTokenService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            RevokedTokenService revokedTokenService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.revokedTokenService = revokedTokenService;
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email().toLowerCase())
                .filter(candidate -> passwordEncoder.matches(request.password(), candidate.getPasswordHash()))
                .orElseThrow(() -> new InvalidCredentialsException());

        if (user.getStatus() == User.Status.LOCKED) {
            throw new AccountLockedException();
        }

        return createLoginResponse(user, "Login successful");
    }

    public LoginResponse register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase();
        String phone = request.phone().trim();

        if (userRepository.existsByEmail(email)) {
            throw new DuplicateAccountException("email");
        }
        if (userRepository.existsByPhone(phone)) {
            throw new DuplicateAccountException("phone");
        }

        User user = new User(
                request.fullName().trim(),
                email,
                phone,
                passwordEncoder.encode(request.password())
        );
        userRepository.save(user);

        return createLoginResponse(user, "Registration successful");
    }

    public LoginResponse refresh(RefreshRequest request) {
        try {
            Claims claims = jwtService.parseClaims(request.refreshToken());
            if (!jwtService.isRefreshToken(claims)) {
                throw new InvalidRefreshTokenException();
            }
            if (revokedTokenService.isRevoked(claims.getId())) {
                throw new InvalidRefreshTokenException();
            }

            User user = userRepository.findByEmail(claims.getSubject())
                    .orElseThrow(InvalidRefreshTokenException::new);
            if (user.getStatus() == User.Status.LOCKED) {
                throw new AccountLockedException();
            }

            revokedTokenService.revoke(claims.getId(), claims.getExpiration());
            return createLoginResponse(user, "Token refreshed");
        } catch (JwtException | IllegalArgumentException exception) {
            throw new InvalidRefreshTokenException();
        }
    }

    public LogoutResponse logout(String accessToken, LogoutRequest request) {
        try {
            Claims accessClaims = jwtService.parseClaims(accessToken);
            Claims refreshClaims = jwtService.parseClaims(request.refreshToken());

            if (!"access".equals(accessClaims.get("tokenType", String.class))) {
                throw new InvalidCredentialsException();
            }
            if (revokedTokenService.isRevoked(accessClaims.getId())) {
                throw new InvalidCredentialsException();
            }
            if (!jwtService.isRefreshToken(refreshClaims)) {
                throw new InvalidRefreshTokenException();
            }
            if (!accessClaims.getSubject().equals(refreshClaims.getSubject())) {
                throw new InvalidCredentialsException();
            }

            revokedTokenService.revoke(accessClaims.getId(), accessClaims.getExpiration());
            revokedTokenService.revoke(refreshClaims.getId(), refreshClaims.getExpiration());
            return new LogoutResponse("Logout successful");
        } catch (JwtException | IllegalArgumentException exception) {
            throw new InvalidRefreshTokenException();
        }
    }

    public UserResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException());
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getRole().name()
        );
    }

    private LoginResponse createLoginResponse(User user, String message) {
        return new LoginResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getRole().name(),
                jwtService.generateAccessToken(user),
                jwtService.generateRefreshToken(user),
                "Bearer",
                jwtService.getAccessTokenExpirationMs() / 1000,
                message
        );
    }
}
