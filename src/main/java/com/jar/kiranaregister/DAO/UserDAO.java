package com.jar.kiranaregister.DAO;

import com.jar.kiranaregister.model.entity.UserEntity;

public interface UserDAO {
    UserEntity save(UserEntity userEntity);
}