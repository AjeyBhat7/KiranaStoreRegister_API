package com.jar.kiranaregister.controller;

import com.jar.kiranaregister.AOP.RateLimited;
import com.jar.kiranaregister.model.DTOModel.UserDto;
import com.jar.kiranaregister.model.requestObj.LoginRequest;
import com.jar.kiranaregister.model.responseModel.UserResponseEntity;
import com.jar.kiranaregister.service.AuthService;
import com.jar.kiranaregister.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.text.MessageFormat;

@RestController
@RequestMapping
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    public UserController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    /**
     * Handles user login requests.
     * This method authenticates the user using the credentials provided in the request body.
     *
     * @param user LoginRequest object containing username and password.
     * @return ResponseEntity with authentication result.
     */
    @RateLimited(bucketName = "LoginRateLimiter")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest user) {
        logger.info("Login attempt for user: {}", user.getPhoneNumber());
        try {
            ResponseEntity<?> response = ResponseEntity.ok(authService.authenticateUser(user));
            logger.info("User {} logged in successfully", user.getPhoneNumber());
            return response;
        } catch (BadCredentialsException ex) {
            logger.warn("Invalid login attempt for user: {}", user.getPhoneNumber());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
    }

    /**
     * Handles user registration requests.
     * This method creates a new user account with the provided details.
     * The password is securely hashed before saving.
     *
     * @param user UserDto object containing user registration details.
     * @return ResponseEntity with the created user details.
     */
    @RateLimited(bucketName = "LoginRateLimiter")
    @PostMapping("/register")
    public ResponseEntity<UserResponseEntity> register(@RequestBody UserDto user) {
        logger.info("Registering new user: {}", user.getUsername());
        try {
            // Hash the user's password before saving
            user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));

            UserResponseEntity response = new UserResponseEntity(userService.save(user));

            logger.info("User {} registered successfully", user.getUsername());
            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (DataIntegrityViolationException ex) {
            logger.warn("User registration failed - username already exists: {}", user.getUsername());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exists");
        }
    }
}
