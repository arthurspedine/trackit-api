package com.spedine.trackit.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserLoginRequest(
        @NotNull
        @Email(message = "Email should be valid")
        String email,
        @NotNull
        @Size(min = 8, message = "Password must be at least 8 characters long")
        String password
) {
}
