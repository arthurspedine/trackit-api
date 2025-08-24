package com.spedine.trackit.service;

import com.spedine.trackit.dto.UserRegisterRequest;
import com.spedine.trackit.model.User;
import com.spedine.trackit.repository.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
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
        if (userRepository.existsByEmail(body.email())) {
            throw new ValidationException("User with this email already exists");
        }
        if (!body.password().equals(body.confirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }
        String encodedPassword = passwordEncoder.encode(body.password());
        User newUser = new User(body.name(), body.email(), encodedPassword);
        userRepository.save(newUser);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
