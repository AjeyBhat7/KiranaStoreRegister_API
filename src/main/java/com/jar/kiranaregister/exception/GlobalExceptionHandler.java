package com.jar.kiranaregister.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Collections;
import java.util.stream.Collectors;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());

        return new ResponseEntity<>(
                new ErrorResponse("Validation failed", errors),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        return new ResponseEntity<>(
                new ErrorResponse("Database error", Collections.singletonList(ex.getMostSpecificCause().getMessage())),
                HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {
        return new ResponseEntity<>(
                new ErrorResponse("Authentication failed", Collections.singletonList("Invalid credentials")),
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralExceptions(Exception ex) {
        return new ResponseEntity<>(
                new ErrorResponse("Internal server error", Collections.singletonList(ex.getMessage())),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    public record ErrorResponse(String error, List<String> details) {}
    public record ErrorResponseList(String error, List<String> details) {}
}