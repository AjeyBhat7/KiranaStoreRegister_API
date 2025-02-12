package com.jar.kiranaregister.model.requestObj;

import lombok.Data;

@Data
public class LoginRequest {
    private String phoneNumber;
    private String password;
}