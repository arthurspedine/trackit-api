package com.spedine.trackit.repository;

import com.spedine.trackit.model.User;

public interface UserRepository {
    User save(User user);
    User findByEmail(String email);
    boolean existsByEmail(String email);
}
