package com.jar.kiranaregister.repository;

import com.jar.kiranaregister.model.Transaction;
import com.jar.kiranaregister.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, String> {
    User findByUserName(String username);
}
