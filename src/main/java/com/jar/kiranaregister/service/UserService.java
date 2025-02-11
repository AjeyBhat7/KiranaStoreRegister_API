package com.jar.kiranaregister.service;

import com.jar.kiranaregister.model.DTOModel.UserDto;
import java.util.List;

public interface UserService {
    List<String> getUserRolesByPhoneNumber(String phoneNumber);

    UserDto save(UserDto userDto);
}