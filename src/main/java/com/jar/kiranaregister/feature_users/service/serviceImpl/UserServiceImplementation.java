package com.jar.kiranaregister.feature_users.service.serviceImpl;

import com.jar.kiranaregister.enums.Role;
import com.jar.kiranaregister.feature_users.dao.UserDao;
import com.jar.kiranaregister.feature_users.model.dto.UserDto;
import com.jar.kiranaregister.feature_users.model.entity.UserEntity;
import com.jar.kiranaregister.feature_users.repository.UserRepository;
import com.jar.kiranaregister.feature_users.service.UserService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.jar.kiranaregister.feature_users.utils.UserUtils.toUserDto;
import static com.jar.kiranaregister.feature_users.utils.UserUtils.toUserEntity;

@Service
@Slf4j
public class UserServiceImplementation implements UserService {

    private final UserDao userDao;
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImplementation(UserDao userDao, UserRepository userRepository) {
        this.userDao = userDao;
        this.userRepository = userRepository;
    }

    /**
     * Saves a new user to the database.
     *
     * @param userDto DTO containing user details.
     * @return The saved user as a DTO.
     */
    @Override
    public UserDto save(UserDto userDto) {

        log.info(" save user: {}", userDto.getUsername());

        UserEntity userEntity = toUserEntity(userDto);

        UserEntity savedUser = userDao.save(userEntity);

        log.info("User saved successfully: {}", savedUser.getId());
        return toUserDto(savedUser);
    }


}
