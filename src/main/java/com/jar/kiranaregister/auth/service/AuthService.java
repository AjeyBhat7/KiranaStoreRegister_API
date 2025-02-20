package com.jar.kiranaregister.auth.service;

import com.jar.kiranaregister.feature_users.model.requestObj.LoginRequest;
import com.jar.kiranaregister.feature_users.model.responseObj.JwtTokenResponse;

public interface AuthService {

    /**
     * Authenticates a user based on their phone number and password. If authentication is
     * successful, JWT token is generated.
     *
     * @param request The login request containing phone number and password.
     * @return JWT token .
     */
    JwtTokenResponse authenticateUser(LoginRequest request);
}
