package com.jar.kiranaregister.exception;

import java.util.NoSuchElementException;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

@RestControllerAdvice
public class GlobalExceptionController {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        ApiResponse response = new ApiResponse();
        response.setSuccess(false);
        response.setStatus("Invalid argument");
        response.setErrorMessage(e.getMessage());
        response.setErrorCode(String.valueOf(HttpStatus.BAD_REQUEST.value()));
        response.setDisplayMsg("An error occurred. Please try again.");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiResponse> handleNoSuchElementException(NoSuchElementException e) {
        ApiResponse response = new ApiResponse();
        response.setSuccess(false);
        response.setStatus("Resource not found");
        response.setErrorMessage(e.getMessage());
        response.setErrorCode(String.valueOf(HttpStatus.NOT_FOUND.value()));
        response.setDisplayMsg("Resource not found");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ExceptionHandler(HttpClientErrorException.Forbidden.class)
    public ResponseEntity<ApiResponse> handleHttpClientErrorException(
            HttpClientErrorException.Forbidden e) {

        ApiResponse response = new ApiResponse();
        response.setSuccess(false);
        response.setStatus("Access Denied");
        response.setErrorMessage("User is not authorized");
        response.setErrorCode(String.valueOf(HttpStatus.FORBIDDEN.value()));

        response.setDisplayMsg("User is not authorized");

        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse> handleBadCredentials(BadCredentialsException e) {
        ApiResponse response = new ApiResponse();
        response.setSuccess(false);
        response.setStatus("error");
        response.setErrorMessage("Invalid credentials");
        response.setErrorCode(String.valueOf(HttpStatus.UNAUTHORIZED.value()));
        response.setDisplayMsg("Invalid credentials");

        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse> handleResourceNotFound(ResourceNotFoundException e) {
        ApiResponse response = new ApiResponse();
        response.setSuccess(false);
        response.setStatus("error");
        response.setErrorMessage("Resource not found");
        response.setErrorCode(String.valueOf(HttpStatus.NOT_FOUND.value()));
        response.setDisplayMsg("Resource not found");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGeneralExceptions(Exception e) {
        ApiResponse response = new ApiResponse();
        response.setSuccess(false);
        response.setStatus("error");
        response.setErrorMessage(e.getMessage());
        response.setErrorCode(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));

        response.setDisplayMsg("An error occurred. Please try again.");
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse> handleRuntimeException(RuntimeException e) {
        ApiResponse response = new ApiResponse();
        response.setSuccess(false);
        response.setStatus("error");
        response.setErrorMessage(e.getMessage());
        response.setErrorCode(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));

        response.setDisplayMsg("An error occurred. Please try again.");
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ApiResponse> handleRateLimitException(RateLimitExceededException e) {
        ApiResponse response = new ApiResponse();
        response.setSuccess(false);
        response.setStatus("Rate Limit Exceeded");
        response.setErrorMessage(e.getMessage());
        response.setErrorCode(String.valueOf(HttpStatus.TOO_MANY_REQUESTS.value()));

        response.setDisplayMsg("An error occurred. Please try again.");

        return new ResponseEntity<>(response, HttpStatus.TOO_MANY_REQUESTS);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse> handleIllegalStateException(IllegalStateException e) {
        ApiResponse response = new ApiResponse();
        response.setSuccess(false);
        response.setStatus("error");
        response.setErrorMessage(e.getMessage());
        response.setErrorCode(String.valueOf(HttpStatus.SERVICE_UNAVAILABLE.value()));
        response.setDisplayMsg("An error occurred. Please try again.");

        return new ResponseEntity<>(response, HttpStatus.SERVICE_UNAVAILABLE);
    }
}
