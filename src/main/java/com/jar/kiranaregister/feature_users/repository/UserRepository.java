package com.jar.kiranaregister.feature_users.repository;

import com.jar.kiranaregister.feature_users.model.entity.UserEntity;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<UserEntity, String> {
    Optional<UserEntity> findByPhoneNumber(String phoneNumber);

    boolean existsByPhoneNumber(String attr0);
}
