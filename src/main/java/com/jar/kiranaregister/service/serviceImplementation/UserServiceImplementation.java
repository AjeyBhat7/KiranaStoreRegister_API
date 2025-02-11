package com.jar.kiranaregister.service.serviceImplementation;

import com.jar.kiranaregister.DAO.UserDAO;
import com.jar.kiranaregister.enums.Role;
import com.jar.kiranaregister.model.DTOModel.UserDto;
import com.jar.kiranaregister.model.entity.UserEntity;
import com.jar.kiranaregister.repository.UserRepository;
import com.jar.kiranaregister.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImplementation implements UserService {

    private final UserDAO userDao;


    @Autowired
    public UserServiceImplementation(UserDAO userDao) {
        this.userDao = userDao;
    }




    @Override
    public UserDto save(UserDto userDto) {

        UserEntity userEntity = toUserEntity(userDto);

        UserEntity savedUser = userDao.save(userEntity);

        return toUserDto(savedUser);
    }

    private UserEntity toUserEntity(UserDto userDto) {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userDto.getId());
        userEntity.setPhoneNumber(userDto.getPhoneNumber());
        userEntity.setUserName(userDto.getUsername());
        userEntity.setPassword(userDto.getPassword());


        List<Role> roles = userDto.getRoles().stream()
                .map(role -> {
                    try {
                        return Role.valueOf(role);
                    } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException("Invalid role: " + role);
                    }
                })
                .toList();

        userEntity.setRoles(roles);

        return userEntity;
    }


    private UserDto toUserDto(UserEntity userEntity) {
        UserDto userDto = new UserDto();
        userDto.setId(userEntity.getId());
        userDto.setPhoneNumber(userEntity.getPhoneNumber());
        userDto.setUsername(userEntity.getUserName());
        userDto.setPassword(userEntity.getPassword());
        userDto.setRoles(userEntity.getRoles().stream().map(Enum::name).collect(Collectors.toList()));
        return userDto;
    }
}