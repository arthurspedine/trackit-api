package com.spedine.trackit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(name = "UserLoginRequest", description = "User credentials for authentication")
public record UserLoginRequest(
        @NotNull
        @Email(message = "Email should be valid")
        @Schema(description = "User email", example = "user@example.com")
        String email,
        @NotNull
        @Size(min = 8, message = "Password must be at least 8 characters long")
        @Schema(description = "User password (min 8 chars)", example = "P@ssw0rd123")
        String password
) {
}
