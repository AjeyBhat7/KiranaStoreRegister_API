package com.jar.kiranaregister.feature_users.model.dto;

import java.util.List;
import lombok.Data;

@Data
public class UserDto {
    private String id;
    private String phoneNumber;
    private String username;
    private String password;
    private List<String> roles;
}
