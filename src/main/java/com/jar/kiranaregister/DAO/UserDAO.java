package com.jar.kiranaregister.DAO;

import com.jar.kiranaregister.model.entity.UserEntity;
import com.jar.kiranaregister.repository.UserRepository;
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


    public UserEntity save(UserEntity userEntity) {

        return userRepository.save(userEntity);
    }

    public Optional<UserEntity> findByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber);
    }
}