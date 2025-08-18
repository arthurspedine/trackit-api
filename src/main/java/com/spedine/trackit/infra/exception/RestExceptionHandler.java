package com.spedine.trackit.infra.exception;

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

    import java.util.List;

    @RestControllerAdvice
    public class RestExceptionHandler {

        private static final Logger log = LoggerFactory.getLogger(RestExceptionHandler.class);

        @ExceptionHandler(EntityNotFoundException.class)
        public ResponseEntity<ExceptionData> entityNotFoundException(EntityNotFoundException e) {
            log.error("Entity not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ExceptionData(e.getMessage()));
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<List<FieldExceptionData>> argumentNotValidException(MethodArgumentNotValidException e) {
            List<FieldError> errors = e.getFieldErrors();
            log.error("Field validation error: {}", errors);
            return ResponseEntity.badRequest().body(errors.stream().map(FieldExceptionData::new).toList());
        }

        @ExceptionHandler(BadCredentialsException.class)
        public ResponseEntity<ExceptionData> badCredentialsException(BadCredentialsException e) {
            log.error("Invalid credentials error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ExceptionData("Invalid credentials!"));
        }

        @ExceptionHandler(AuthenticationException.class)
        public ResponseEntity<ExceptionData> authenticationException(AuthenticationException e) {
            log.error("Invalid authentication error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ExceptionData("Invalid authentication! Check the fields and try again!"));
        }

        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<ExceptionData> accessDeniedException(AccessDeniedException e) {
            log.error("Access denied error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ExceptionData("Access denied!"));
        }

        @ExceptionHandler(ValidationException.class)
        public ResponseEntity<ExceptionData> validationException(ValidationException e) {
            log.error("Validation error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ExceptionData(e.getMessage()));
        }

        @ExceptionHandler(RuntimeException.class)
        public ResponseEntity<ExceptionData> runtimeException(RuntimeException e) {
            log.error("Runtime error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ExceptionData(e.getLocalizedMessage()));
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ExceptionData> exception(Exception ex) {
            log.error("General error: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ExceptionData(ex.getLocalizedMessage()));
        }
    }