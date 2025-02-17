package com.jar.kiranaregister.feature_users.controller;

import com.jar.kiranaregister.ratelimiting.AOP.RateLimited;
import com.jar.kiranaregister.feature_users.model.dto.UserDto;
import com.jar.kiranaregister.feature_users.model.requestObj.LoginRequest;
import com.jar.kiranaregister.feature_auth.service.AuthService;
import com.jar.kiranaregister.feature_users.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

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

            ResponseEntity<?> response = ResponseEntity.ok(authService.authenticateUser(user));
            logger.info("User {} logged in successfully", user.getPhoneNumber());
            return response;
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
    public ResponseEntity<?> register(@RequestBody UserDto user)  {
        logger.info("Registering new user: {}", user.getUsername());

            // Hash the user's password before saving
            user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
            userService.save(user);
            
            logger.info("User {} registered successfully", user.getUsername());

            return new ResponseEntity<>("User regisetrd succussfully", HttpStatus.CREATED);

    }
}
