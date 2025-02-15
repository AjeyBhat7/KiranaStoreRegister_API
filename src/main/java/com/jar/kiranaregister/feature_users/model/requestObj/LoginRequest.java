package com.jar.kiranaregister.feature_users.model.requestObj;

import lombok.Data;

@Data
public class LoginRequest {
    private String phoneNumber;
    private String password;
}