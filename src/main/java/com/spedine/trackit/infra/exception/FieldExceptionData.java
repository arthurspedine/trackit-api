package com.spedine.trackit.infra.exception;

import org.springframework.validation.FieldError;

public record FieldExceptionData(
        String field,
        String message
) {
    public FieldExceptionData(FieldError field) {
        this(field.getField(), field.getDefaultMessage());
    }
}
