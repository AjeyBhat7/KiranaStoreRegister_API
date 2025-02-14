package com.jar.kiranaregister.feature_auth.service;

import com.jar.kiranaregister.feature_transaction.model.requestObj.LoginRequest;

public interface AuthService {


    String authenticateUser(LoginRequest request);

    String login(LoginRequest request);
}
