package com.jar.kiranaregister.model.DTOModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String token;
    private String message;
    private Long userId;
    private boolean success;
    private boolean isAdmin;

    public LoginResponse(String message, boolean success) {
        this.message = message;
        this.success = success;
    }
}
