package com.jar.kiranaregister.feature_users.dao;

import com.jar.kiranaregister.feature_users.model.entity.UserEntity;
import com.jar.kiranaregister.feature_users.repository.UserRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserDao {

    private final UserRepository userRepository;

    @Autowired
    public UserDao(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserEntity save(UserEntity userEntity) {

        if (userRepository.existsByPhoneNumber(userEntity.getPhoneNumber())) {
            throw new IllegalArgumentException("This phone number alredy used");
        }
        return userRepository.save(userEntity);
    }

    /**
     * fetch the user by phone number
     * @param phoneNumber
     * @return
     */
    public Optional<UserEntity> findByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber);
    }

    /**
     * fetch the user by id
     * @param userId
     * @return
     */
    public Optional<UserEntity> findById(String userId) {
        return userRepository.findById(userId);
    }
}
