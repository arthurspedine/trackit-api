package com.spedine.trackit.controller;

import com.spedine.trackit.dto.MessageResponse;
import com.spedine.trackit.dto.UserLoginRequest;
import com.spedine.trackit.dto.UserRegisterRequest;
import com.spedine.trackit.model.UserEntity;
import com.spedine.trackit.service.TokenService;
import com.spedine.trackit.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public AuthenticationController(UserService userService, AuthenticationManager authenticationManager, TokenService tokenService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    @PostMapping("/register")
    public ResponseEntity<MessageResponse> register(@RequestBody @Valid UserRegisterRequest body) {
        userService.registerUser(body);
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse("User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody @Valid UserLoginRequest body) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(body.email(), body.password());
        Authentication auth = authenticationManager.authenticate(authToken);
        String token = tokenService.genToken(((UserEntity) auth.getPrincipal()).toDomain());
        return ResponseEntity.ok(Map.of("token", token));
    }
}
