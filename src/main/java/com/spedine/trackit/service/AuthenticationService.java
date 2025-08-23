package com.spedine.trackit.service;

import com.spedine.trackit.dto.UserLoginRequest;
import com.spedine.trackit.model.User;
import com.spedine.trackit.model.UserEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public AuthenticationService(AuthenticationManager authenticationManager, TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    public String authenticateAndGenerateToken(UserLoginRequest loginRequest) {
        UsernamePasswordAuthenticationToken authToken = 
            new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password());
        
        Authentication authentication = authenticationManager.authenticate(authToken);
        
        UserEntity userEntity = (UserEntity) authentication.getPrincipal();
        User user = userEntity.toDomain();
        
        return tokenService.genToken(user);
    }
}
