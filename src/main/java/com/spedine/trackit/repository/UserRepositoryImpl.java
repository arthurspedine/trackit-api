package com.spedine.trackit.repository;

import com.spedine.trackit.model.User;
import com.spedine.trackit.model.UserEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Repository;

@Repository
class UserRepositoryImpl implements UserRepository, UserDetailsService {

    private final UserJpaRepository userJpaRepository;

    public UserRepositoryImpl(UserJpaRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    public User save(User user) {
        UserEntity userEntity = UserEntity.fromDomain(user);
        return userJpaRepository.save(userEntity).toDomain();
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        return userJpaRepository.findByEmail(email);
    }

    @Override
    public User findByEmail(String email) {
        return userJpaRepository.findUserEntityByEmail(email).toDomain();
    }
}
