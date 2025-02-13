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

    @RateLimited(bucketName = "LoginRateLimiter")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest user) {
        try {
            return ResponseEntity.ok(authService.authenticateUser(user));

        } catch (BadCredentialsException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
    }
    
    @RateLimited(bucketName = "LoginRateLimiter")
    @PostMapping("/register")
    public ResponseEntity<UserResponseEntity> register(@RequestBody UserDto user) {
        try {
            user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
            UserResponseEntity response = new UserResponseEntity(userService.save(user));
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exists");
        }
    }
}