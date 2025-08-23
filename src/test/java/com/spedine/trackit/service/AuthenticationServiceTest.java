package com.spedine.trackit.service;

import com.spedine.trackit.dto.UserLoginRequest;
import com.spedine.trackit.model.User;
import com.spedine.trackit.model.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TokenService tokenService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthenticationService authenticationService;

    private UserLoginRequest loginRequest;
    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        loginRequest = new UserLoginRequest("test@example.com", "password");
        userEntity = UserEntity.fromDomain(
                new User(
                        "Test User",
                        "test@example.com",
                        "encodedPassword"
                ));
    }

    @Test
    @DisplayName("Should authenticate and generate token when valid credentials are provided")
    void authenticateAndGenerateToken_ShouldReturnToken_WhenValidCredentials() {
        String expectedToken = "jwt.token.here";

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userEntity);
        when(tokenService.genToken(any(User.class))).thenReturn(expectedToken);

        String actualToken = authenticationService.authenticateAndGenerateToken(loginRequest);

        assertEquals(expectedToken, actualToken);

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenService).genToken(any(User.class));
    }

    @Test
    @DisplayName("Should create correct authentication token with provided credentials")
    void authenticateAndGenerateToken_ShouldCreateCorrectAuthenticationToken() {
        String expectedToken = "jwt.token.here";

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userEntity);
        when(tokenService.genToken(any(User.class))).thenReturn(expectedToken);

        authenticationService.authenticateAndGenerateToken(loginRequest);

        verify(authenticationManager).authenticate(argThat(token ->
                token instanceof UsernamePasswordAuthenticationToken &&
                        token.getPrincipal().equals("test@example.com") &&
                        token.getCredentials().equals("password")
        ));
    }
}
