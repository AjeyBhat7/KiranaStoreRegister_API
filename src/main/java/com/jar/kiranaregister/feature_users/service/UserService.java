package com.jar.kiranaregister.feature_users.service;

import com.jar.kiranaregister.feature_users.model.dto.UserDto;

public interface UserService {

    /**
     * Store user details in db
     * @param userDto
     * @return
     */
    UserDto save(UserDto userDto);
}
