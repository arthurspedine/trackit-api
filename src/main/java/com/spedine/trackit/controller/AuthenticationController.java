package com.spedine.trackit.controller;

import com.spedine.trackit.dto.MessageResponse;
import com.spedine.trackit.dto.UserLoginRequest;
import com.spedine.trackit.dto.UserRegisterRequest;
import com.spedine.trackit.service.AuthenticationService;
import com.spedine.trackit.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Public endpoints for user registration and login. No bearer token required.")
public class AuthenticationController {

    private final UserService userService;
    private final AuthenticationService authenticationService;

    public AuthenticationController(UserService userService, AuthenticationService authenticationService) {
        this.userService = userService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    @Operation(
            summary = "Register user",
            description = "Create a new user account. Public route (no bearer token required)."
    )
    public ResponseEntity<MessageResponse> register(@RequestBody @Valid UserRegisterRequest body) {
        userService.registerUser(body);
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse("User registered successfully"));
    }

    @PostMapping("/login")
    @Operation(
            summary = "Login",
            description = "Authenticate with email and password to receive a JWT token. Public route (no bearer token required)."
    )
    public ResponseEntity<Map<String, String>> login(@RequestBody @Valid UserLoginRequest body) {
        String token = authenticationService.authenticateAndGenerateToken(body);
        return ResponseEntity.ok(Map.of("token", token));
    }
}
