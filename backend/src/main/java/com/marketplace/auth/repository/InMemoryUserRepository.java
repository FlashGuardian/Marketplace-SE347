package com.marketplace.auth.repository;

import com.marketplace.auth.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

@Repository
public class InMemoryUserRepository implements UserRepository {

    private final Map<String, User> users;

    public InMemoryUserRepository(PasswordEncoder passwordEncoder) {
        User demoUser = new User(
                1L,
                "demo@marketplace.local",
                passwordEncoder.encode("password"),
                "Demo User"
        );
        this.users = Map.of(demoUser.email(), demoUser);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(users.get(email.toLowerCase()));
    }
}
