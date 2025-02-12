package com.jar.kiranaregister.service;

import com.jar.kiranaregister.model.requestObj.LoginRequest;

public interface AuthService {


    String authenticateUser(LoginRequest request);

    String login(LoginRequest request);
}
