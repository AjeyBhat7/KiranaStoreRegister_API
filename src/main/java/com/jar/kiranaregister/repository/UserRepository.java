package com.jar.kiranaregister.repository;

import com.jar.kiranaregister.model.entity.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<UserEntity, String> {
    Optional<UserEntity> findByPhoneNumber(String phoneNumber);
}