package com.spedine.trackit.infra.exception;

import com.spedine.trackit.infra.util.DateTimeUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

@RestControllerAdvice
public class RestExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(RestExceptionHandler.class);

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ExceptionData> entityNotFoundException(EntityNotFoundException e,
                                                                 HttpServletRequest request) {
        log.error("Entity not found: {}", e.getMessage());
        ExceptionData data = new ExceptionData(
                DateTimeUtils.nowFormatted(),
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                e.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(data);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<FieldExceptionData>> argumentNotValidException(MethodArgumentNotValidException e) {
        List<FieldError> errors = e.getFieldErrors();
        log.error("Field validation error: {}", errors);
        return ResponseEntity.badRequest().body(errors.stream().map(FieldExceptionData::new).toList());
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ExceptionData> validationException(ValidationException e,
                                                             HttpServletRequest request) {
        log.error("Validation error: {}", e.getMessage());
        ExceptionData data = new ExceptionData(
                DateTimeUtils.nowFormatted(),
                HttpStatus.CONFLICT.value(),
                "Conflict",
                e.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(data);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionData> badCredentialsException(BadCredentialsException e,
                                                                 HttpServletRequest request) {
        log.error("Invalid credentials error: {}", e.getMessage());
        ExceptionData data = new ExceptionData(
                DateTimeUtils.nowFormatted(),
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized",
                "Invalid credentials!",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(data);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ExceptionData> authenticationException(AuthenticationException e,
                                                                 HttpServletRequest request) {
        log.error("Invalid authentication error: {}", e.getMessage());
        ExceptionData data = new ExceptionData(
                DateTimeUtils.nowFormatted(),
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized",
                "Invalid authentication! Check the fields and try again!",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(data);
    }

    @ExceptionHandler(JwtAuthenticationException.class)
    public ResponseEntity<ExceptionData> handleJwtAuthenticationException(JwtAuthenticationException e,
                                                                          HttpServletRequest request) {
        log.error("JWT auth error: {}", e.getMessage());
        ExceptionData data = new ExceptionData(
                DateTimeUtils.nowFormatted(),
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized",
                e.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(data);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ExceptionData> accessDeniedException(AccessDeniedException e,
                                                               HttpServletRequest request) {
        log.error("Access denied error: {}", e.getMessage());
        ExceptionData data = new ExceptionData(
                DateTimeUtils.nowFormatted(),
                HttpStatus.FORBIDDEN.value(),
                "Forbidden",
                "Access denied!",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(data);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ExceptionData> runtimeException(RuntimeException e,
                                                          HttpServletRequest request) {
        log.error("Runtime error: {}", e.getMessage());
        ExceptionData data = new ExceptionData(
                DateTimeUtils.nowFormatted(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                e.getLocalizedMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(data);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionData> exception(Exception ex,
                                                   HttpServletRequest request) {
        log.error("General error: {}", ex.getMessage());
        ExceptionData data = new ExceptionData(
                DateTimeUtils.nowFormatted(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                ex.getLocalizedMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(data);
    }
}
