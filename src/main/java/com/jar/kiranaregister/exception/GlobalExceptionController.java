package com.jar.kiranaregister.exception;

import static com.jar.kiranaregister.exception.constants.ExceptionConstants.*;

import com.jar.kiranaregister.response.ApiResponse;
import java.text.MessageFormat;
import java.util.NoSuchElementException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

/** exception controller catch exception globally */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionController {

    /** Exception handler for Illeagle argument exception */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error(MessageFormat.format(INVALID_ARGUMENT, e.getMessage()));

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
        log.error(MessageFormat.format(RESOURCE_NOT_FOUND, e.getMessage()), e);

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
        log.error(MessageFormat.format(ACCESS_DENIED, e.getMessage()));

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
        log.error(MessageFormat.format(INVALID_CREDENTIALS, e.getMessage()));

        ApiResponse response = new ApiResponse();
        response.setSuccess(false);
        response.setStatus("error");
        response.setErrorMessage("Invalid credentials");
        response.setErrorCode(String.valueOf(HttpStatus.UNAUTHORIZED.value()));
        response.setDisplayMsg("Invalid credentials");

        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse> handleUserNotFoundException(UserNotFoundException e) {
        log.error(MessageFormat.format(RESOURCE_NOT_FOUND, e.getMessage()));
        ApiResponse response = new ApiResponse();
        response.setSuccess(false);
        response.setStatus("error");
        response.setErrorMessage("User not found");
        response.setErrorCode(String.valueOf(HttpStatus.NOT_FOUND.value()));
        response.setDisplayMsg("User not found");

        return new ResponseEntity<>(response, HttpStatus.OK);

    }



    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse> handleResourceNotFound(ResourceNotFoundException e) {
        log.error(MessageFormat.format(RESOURCE_NOT_FOUND, e.getMessage()));

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
        log.error(MessageFormat.format(UNHANDLED_EXCEPTION, e.getMessage()));

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
        log.error(MessageFormat.format(RUNTIME_EXCEPTION, e.getMessage()));

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
        log.error(MessageFormat.format(RATE_LIMIT_EXCEEDED, e.getMessage()));

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
        log.error(MessageFormat.format(ILLEGAL_STATE, e.getMessage()));

        ApiResponse response = new ApiResponse();
        response.setSuccess(false);
        response.setStatus("error");
        response.setErrorMessage(e.getMessage());
        response.setErrorCode(String.valueOf(HttpStatus.SERVICE_UNAVAILABLE.value()));
        response.setDisplayMsg("An error occurred. Please try again.");

        return new ResponseEntity<>(response, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ApiResponse> handleAuthorizationDeniedException(
            AuthorizationDeniedException e) {
        log.error(MessageFormat.format(AUTHORIZATION_DENIED, e.getMessage()));

        ApiResponse response = new ApiResponse();
        response.setSuccess(false);
        response.setStatus("error");
        response.setErrorMessage(e.getMessage());
        response.setErrorCode(String.valueOf(HttpStatus.FORBIDDEN.value()));
        response.setDisplayMsg("User does not have permission to perform this action");

        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }
}
