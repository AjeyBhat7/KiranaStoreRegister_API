package com.jar.kiranaregister.DAO;

import com.jar.kiranaregister.model.entity.UserEntity;
import com.jar.kiranaregister.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserDAOImplementation implements UserDAO {

    private final UserRepository userRepository;

    @Autowired
    public UserDAOImplementation(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserEntity save(UserEntity userEntity) {
        return userRepository.save(userEntity);
    }
}