package com.jar.kiranaregister.model.responseModel;

import com.jar.kiranaregister.model.DTOModel.UserDto;
import com.jar.kiranaregister.model.entity.UserEntity;
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
