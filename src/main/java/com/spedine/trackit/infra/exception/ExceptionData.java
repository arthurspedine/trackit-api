package com.spedine.trackit.infra.exception;

public record ExceptionData(
        String timestamp,
        int status,
        String error,
        String message,
        String path
) {
}
