package com.jar.kiranaregister.exception;

import com.nimbusds.oauth2.sdk.util.singleuse.AlreadyUsedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNoSuchElementException(NoSuchElementException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException e) {

        return new ResponseEntity<>(new ErrorResponse("Authentication failed", Collections.singletonList("Invalid credentials")),
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralExceptions(Exception e) {
        return new ResponseEntity<>(
                new ErrorResponse("Internal server error", Collections.singletonList(e.getMessage())),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRateLimitException(RuntimeException e) {
        if(e.getMessage() == "Rate limit exceeded. Try again later.")
            return new ResponseEntity<>(e.getMessage(), HttpStatus.TOO_MANY_REQUESTS);

        return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        return new ResponseEntity<>("user already exists", HttpStatus.BAD_REQUEST);
    }


    public record ErrorResponse(String error, List<String> details) {}
    public record ErrorResponseList(String error, List<String> details) {}
}