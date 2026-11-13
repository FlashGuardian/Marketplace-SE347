package com.marketplace.auth.repository;

import com.marketplace.auth.model.User;

import java.util.Optional;

public interface UserRepository {

    Optional<User> findByEmail(String email);
}
