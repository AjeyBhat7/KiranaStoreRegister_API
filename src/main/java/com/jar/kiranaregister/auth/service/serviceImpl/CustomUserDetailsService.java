package com.jar.kiranaregister.auth.service.serviceImpl;

import com.jar.kiranaregister.feature_users.dao.UserDao;
import com.jar.kiranaregister.feature_users.model.entity.UserEntity;
import com.jar.kiranaregister.feature_users.model.entity.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserDao userDao;

    @Autowired
    public CustomUserDetailsService(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * lodes user by username
     *
     * @param userId
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {

        UserEntity user =
                userDao.findById(userId).orElseThrow(() -> new UsernameNotFoundException(userId));

        return User.withUsername(user.getId())
                .password(user.getPassword())
                .roles(user.getRoles().stream().map(Enum::name).toArray(String[]::new))
                .build();
    }

    /**
     * loads user by user id.
     *
     * @param userId
     * @return
     */
    public UserInfo loadUserByUserId(String userId) {
        UserEntity user = userDao.findById(userId).orElse(null);

        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        return new UserInfo(user);
    }
}
