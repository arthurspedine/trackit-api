package com.spedine.trackit.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserRegisterRequest(
        @NotNull
        @Size(min = 5, message = "Username must be at least 5 characters long")
        String name,
        @NotNull
        @Email(message = "Email should be valid")
        String email,
        @NotNull
        @Size(min = 8, message = "Password must be at least 8 characters long")
        String password,
        @NotNull
        @Size(min = 8, message = "Confirm password must be at least 8 characters long")
        String confirmPassword
) {
}
