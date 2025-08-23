package com.spedine.trackit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(name = "UserRegisterRequest", description = "Payload to create a new user account")
public record UserRegisterRequest(
        @NotNull
        @Size(min = 5, message = "Username must be at least 5 characters long")
        @Schema(description = "Full name of the user", example = "John Doe")
        String name,
        @NotNull
        @Email(message = "Email should be valid")
        @Schema(description = "Email address", example = "john.doe@example.com")
        String email,
        @NotNull
        @Size(min = 8, message = "Password must be at least 8 characters long")
        @Schema(description = "Password (min 8 chars)", example = "P@ssw0rd123")
        String password,
        @NotNull
        @Size(min = 8, message = "Confirm password must be at least 8 characters long")
        @Schema(description = "Password confirmation (must match password)", example = "P@ssw0rd123")
        String confirmPassword
) {
}
