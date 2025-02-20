package com.jar.kiranaregister.feature_users.service.serviceImpl;

import static com.jar.kiranaregister.feature_users.constants.UserConstants.LOG_USER_CREATED;
import static com.jar.kiranaregister.feature_users.utils.UserUtils.toUserDto;
import static com.jar.kiranaregister.feature_users.utils.UserUtils.toUserEntity;

import com.jar.kiranaregister.feature_users.dao.UserDao;
import com.jar.kiranaregister.feature_users.model.dto.UserDto;
import com.jar.kiranaregister.feature_users.model.entity.UserEntity;
import com.jar.kiranaregister.feature_users.service.UserService;
import java.text.MessageFormat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserServiceImplementation implements UserService {

    private final UserDao userDao;

    @Autowired
    public UserServiceImplementation(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * Saves a new user to the database.
     *
     * @param userLoginRequest DTO containing user details.
     * @return The saved user as a DTO.
     */
    @Override
    public UserDto save(UserDto userLoginRequest) {

        userLoginRequest.setPassword(
                new BCryptPasswordEncoder().encode(userLoginRequest.getPassword()));

        UserEntity userEntity = toUserEntity(userLoginRequest);

        UserEntity savedUser = userDao.save(userEntity);

        log.info(MessageFormat.format(LOG_USER_CREATED, savedUser.getId()));

        return toUserDto(savedUser);
    }
}
