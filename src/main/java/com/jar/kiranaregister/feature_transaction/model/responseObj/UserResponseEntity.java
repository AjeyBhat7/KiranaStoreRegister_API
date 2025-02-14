package com.jar.kiranaregister.feature_transaction.model.responseObj;

import com.jar.kiranaregister.feature_users.model.dto.UserDto;
import com.jar.kiranaregister.feature_users.model.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseEntity {

    private UserDto userEntity;

    public UserResponseEntity(UserEntity userEntity) {}
}
