package com.jar.kiranaregister.model.DTOModel;

import lombok.Data;

import java.util.List;

@Data
public class UserDto {
    private String id;
    private String phoneNumber;
    private String username;
    private String password;
    private List<String> roles;
}