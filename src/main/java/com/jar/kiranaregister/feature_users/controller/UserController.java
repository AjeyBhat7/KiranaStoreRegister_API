package com.jar.kiranaregister.feature_users.controller;

import com.jar.kiranaregister.auth.service.AuthService;
import com.jar.kiranaregister.feature_users.model.dto.UserDto;
import com.jar.kiranaregister.feature_users.model.requestObj.LoginRequest;
import com.jar.kiranaregister.feature_users.service.UserService;
import com.jar.kiranaregister.ratelimiting.AOP.RateLimited;
import com.jar.kiranaregister.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.jar.kiranaregister.feature_users.constants.UserConstants.USER_REGISTRATION_SUCCUSSFUL;

@RestController
@RequestMapping("api/v1")
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
    @PostMapping("login")
    public ResponseEntity<ApiResponse> login(@RequestBody LoginRequest user) {

        ApiResponse response = new ApiResponse();
        response.setData(authService.authenticateUser(user));
        response.setStatus(HttpStatus.OK.name());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Handles user registration requests. This method creates a new user account The password is
     * hashed before saving.
     *
     * @return ResponseEntity with the created user details.
     */
    @RateLimited(bucketName = "LoginRateLimiter")
    @PostMapping("register")
    public ResponseEntity<ApiResponse> register(@RequestBody UserDto user) {

        userService.save(user);

        ApiResponse response = new ApiResponse();
        response.setStatus(HttpStatus.CREATED.name());
        response.setDisplayMsg(USER_REGISTRATION_SUCCUSSFUL);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
