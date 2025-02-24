package com.jar.kiranaregister.auth.service.serviceImpl;

import static com.jar.kiranaregister.feature_users.constants.UserConstants.JWT_TOKEN_GENERATED;

import com.jar.kiranaregister.auth.service.AuthService;
import com.jar.kiranaregister.auth.utils.JwtUtil;
import com.jar.kiranaregister.exception.UserNotFoundException;
import com.jar.kiranaregister.feature_users.dao.UserDao;
import com.jar.kiranaregister.feature_users.model.entity.UserEntity;
import com.jar.kiranaregister.feature_users.model.requestObj.LoginRequest;
import com.jar.kiranaregister.feature_users.model.responseObj.JwtTokenResponse;
import com.jar.kiranaregister.feature_users.repository.UserRepository;
import java.text.MessageFormat;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthServiceImplementation implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final UserDao userDAO;

    @Autowired
    public AuthServiceImplementation(
            AuthenticationManager authenticationManager,
            JwtUtil jwtUtil,
            UserRepository userRepository,
            UserDao userDAO) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.userDAO = userDAO;
    }

    /**
     * Authenticates a user based on their phone number and password. If authentication is
     * successful, a JWT token is generated.
     *
     * @param request The login request containing phone number and password.
     * @return JWT token .
     */
    @Override
    public JwtTokenResponse authenticateUser(LoginRequest request) {

        // Retrieve user from the database
        UserEntity user = userDAO.findByPhoneNumber(request.getPhoneNumber()).orElse(null);

        String userId = Optional.ofNullable(user).map(UserEntity::getId).orElse(null);

        if (userId == null) {
            log.warn(" User not found for phone number: {}", request.getPhoneNumber());
            throw new UserNotFoundException("User not found for the phone number");
        }

        // Authenticate user
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userId, request.getPassword()));

        log.info("User {} authenticated successfully", request.getPhoneNumber());

        // Retrieve user details
        UserEntity userEntity =
                userRepository.findByPhoneNumber(request.getPhoneNumber()).orElse(null);
        if (userEntity == null) {
            throw new UsernameNotFoundException("User not found");
        }

        // Generate JWT Token
        String token =
                jwtUtil.generateToken(
                        userEntity.getId(),
                        userEntity.getRoles().stream()
                                .map(Enum::name)
                                .collect(Collectors.toList()));

        log.info(MessageFormat.format(JWT_TOKEN_GENERATED, userEntity.getId()));

        JwtTokenResponse jwtTokenResponse = new JwtTokenResponse();
        jwtTokenResponse.setJwtToken(token);
        return jwtTokenResponse;
    }
}
