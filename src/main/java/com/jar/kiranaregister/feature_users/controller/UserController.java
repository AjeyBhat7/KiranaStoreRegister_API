package com.jar.kiranaregister.feature_users.controller;

import com.jar.kiranaregister.feature_auth.service.AuthService;
import com.jar.kiranaregister.feature_users.model.dto.UserDto;
import com.jar.kiranaregister.feature_users.model.requestObj.LoginRequest;
import com.jar.kiranaregister.feature_users.service.UserService;
import com.jar.kiranaregister.ratelimiting.AOP.RateLimited;
import com.jar.kiranaregister.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
@Slf4j
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    @Autowired
    public UserController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    /**
     * Handles user login requests. This method authenticates the user using the credentials
     * provided in the request body.
     *
     * @param user LoginRequest object containing username and password.
     * @return ResponseEntity with authentication result.
     */
    @RateLimited(bucketName = "LoginRateLimiter")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody LoginRequest user) {
        log.info("Login attempt for user: {}", user.getPhoneNumber());
        String jwtToken = authService.authenticateUser(user);
        log.info("User {} logged in successfully", user.getPhoneNumber());

        ApiResponse response = new ApiResponse();
        response.setData(jwtToken);
        response.setStatus(HttpStatus.OK.name());
        response.setData(jwtToken);
        response.setSuccess(true);

        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    /**
     * Handles user registration requests. This method creates a new user account
     * The password is hashed before saving.
     *
     * @param user
     * @return ResponseEntity with the created user details.
     */
    @RateLimited(bucketName = "LoginRateLimiter")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@RequestBody UserDto user) {
        log.info("Registering new user: {}", user.getUsername());

        // Hash the users password before saving
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        userService.save(user);

        log.info("User {} registered successfully", user.getUsername());

        ApiResponse response = new ApiResponse();
        response.setData(user);
        response.setStatus(HttpStatus.CREATED.name());
        response.setSuccess(true);
        response.setDisplayMsg("User registered successfully");

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
