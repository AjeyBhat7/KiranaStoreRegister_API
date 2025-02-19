package com.jar.kiranaregister.feature_auth.service;

import com.jar.kiranaregister.feature_users.model.requestObj.LoginRequest;

public interface AuthService {

    /**
     * Authenticates a user based on their phone number and password.
     *  If authentication is successful,  JWT token is generated.
     *
     * @param request The login request containing phone number and password.
     * @return JWT token .
     */
    String authenticateUser(LoginRequest request);

}