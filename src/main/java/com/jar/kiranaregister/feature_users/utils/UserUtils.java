package com.jar.kiranaregister.feature_users.utils;

import com.jar.kiranaregister.enums.Role;
import com.jar.kiranaregister.feature_users.model.dto.UserDto;
import com.jar.kiranaregister.feature_users.model.entity.UserEntity;

import java.util.List;
import java.util.stream.Collectors;

public class UserUtils {

    /**
     * Converts a UserDto to a UserEntity for db.
     *
     * @param userDto The DTO to convert.
     * @return The converted UserEntity.
     */
    public static UserEntity toUserEntity(UserDto userDto) {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userDto.getId());
        userEntity.setPhoneNumber(userDto.getPhoneNumber());
        userEntity.setUserName(userDto.getUsername());
        userEntity.setPassword(userDto.getPassword());

        List<Role> roles = userDto.getRoles().stream().map(Role::valueOf).toList();

        userEntity.setRoles(roles);

        return userEntity;
    }

    /**
     * Converts a UserEntity to a UserDto
     *
     * @param userEntity The entity to convert.
     * @return The converted UserDto.
     */
    public static UserDto toUserDto(UserEntity userEntity) {
        UserDto userDto = new UserDto();
        userDto.setId(userEntity.getId());
        userDto.setPhoneNumber(userEntity.getPhoneNumber());
        userDto.setUsername(userEntity.getUserName());
        userDto.setPassword(userEntity.getPassword());
        userDto.setRoles(
                userEntity.getRoles().stream().map(Enum::name).collect(Collectors.toList()));
        return userDto;
    }
}
