package com.jar.kiranaregister.feature_users.dao;

import com.jar.kiranaregister.feature_users.model.entity.UserEntity;
import com.jar.kiranaregister.feature_users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserDAO {

    private final UserRepository userRepository;

    @Autowired
    public UserDAO(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public UserEntity save(UserEntity userEntity)  {

        if(userRepository.existsByPhoneNumber(userEntity.getPhoneNumber())){
            throw new IllegalArgumentException("This phone number alredy used");
        }
        return userRepository.save(userEntity);
    }

    public Optional<UserEntity> findByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber);
    }

    public Optional<UserEntity> findById(String userId) {
        return userRepository.findById(userId);
    }
}