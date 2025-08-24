package com.spedine.trackit.repository;

import com.spedine.trackit.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;

interface UserJpaRepository extends JpaRepository<UserEntity, UUID> {
    UserDetails findByEmail(String email);

    UserEntity findUserEntityByEmail(String email);

    boolean existsByEmail(String email);
}
