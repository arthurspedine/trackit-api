package com.spedine.trackit.service;

import com.spedine.trackit.dto.UserRegisterRequest;
import com.spedine.trackit.model.User;
import com.spedine.trackit.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void registerUser(@Valid UserRegisterRequest body) {
        UserDetails user = userRepository.findByEmail(body.email());
        if (user != null) {
            throw new IllegalArgumentException("User with this email already exists");
        }
        User newUser = new User();
        newUser.setName(body.name());
        newUser.setEmail(body.email());
        if (!body.password().equals(body.confirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }
        String encodedPassword = passwordEncoder.encode(body.password());
        newUser.setPassword(encodedPassword);
        userRepository.save(newUser);
    }
}
