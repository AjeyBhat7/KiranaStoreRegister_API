package com.jar.kiranaregister.service.serviceImplementation;

import java.util.ArrayList;

import com.jar.kiranaregister.exception.CustomException;
import com.jar.kiranaregister.model.User;
import com.jar.kiranaregister.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired private UserRepository authRepo;

    public UserDetails loadUserByUsername(String username) {
        User user = authRepo.findByUserName(username);
        if (user == null) {
            throw new CustomException("User name is invalid");
        }
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(), new ArrayList<>());
    }
}