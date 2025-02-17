package com.jar.kiranaregister.feature_auth.service;

import com.jar.kiranaregister.feature_users.dao.UserDAO;
import com.jar.kiranaregister.feature_users.model.entity.UserEntity;
import com.jar.kiranaregister.feature_users.model.entity.UserInfo;
import com.jar.kiranaregister.feature_users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserDAO userDAO;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository, UserDAO userDAO) {
        this.userRepository = userRepository;
        this.userDAO = userDAO;
    }

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {

        Optional<UserEntity> userEntity = userRepository.findById(userId);

        if (userEntity.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }

        UserEntity user = userEntity.get();

        return User.withUsername(user.getId())
                .password(user.getPassword())
                .roles(user.getRoles().stream().map(Enum::name).toArray(String[]::new))
                .build();
    }

    public UserInfo loadUserByUserId(String userId) {
        UserEntity user = userDAO.findById(userId).orElse(null);

        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }


        return new UserInfo(user);
    }
}

