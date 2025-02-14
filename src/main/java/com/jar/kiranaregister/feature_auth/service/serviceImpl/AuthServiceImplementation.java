package com.jar.kiranaregister.feature_auth.service.serviceImpl;

import com.jar.kiranaregister.feature_auth.service.AuthService;
import com.jar.kiranaregister.feature_users.model.dto.UserDto;
import com.jar.kiranaregister.feature_users.model.entity.UserEntity;
import com.jar.kiranaregister.feature_transaction.model.requestObj.LoginRequest;
import com.jar.kiranaregister.feature_users.repository.UserRepository;
import com.jar.kiranaregister.feature_transaction.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AuthServiceImplementation implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Autowired
    public AuthServiceImplementation(
            AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    /**
     * Authenticates a user based on their phone number and password.
     * If authentication is successful, a JWT token is generated.
     *
     * @param request The login request containing phone number and password.
     * @return  JWT token .
     */
    @Override
    public String authenticateUser(LoginRequest request) {
        log.info("Attempting authentication for phone number: {}", request.getPhoneNumber());

        // Retrieve user from the database
        UserEntity user = userRepository.findByPhoneNumber(request.getPhoneNumber()).orElse(null);
        String userId = Optional.ofNullable(user).map(UserEntity::getId).orElse(null);

        if (userId == null) {
            log.warn(" User not found for phone number: {}", request.getPhoneNumber());
            throw new UsernameNotFoundException("User not found for the phone number");
        }

        // Authenticate user
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userId, request.getPassword()));

        log.info("User {} authenticated successfully", request.getPhoneNumber());

        // Retrieve user details
        UserEntity userEntity = userRepository.findByPhoneNumber(request.getPhoneNumber())
                .orElseThrow(() -> {
                    log.error(" User not found .");
                    return new UsernameNotFoundException("User not found");
                });

        // Convert UserEntity to UserDto
        UserDto userDto = new UserDto();
        userDto.setId(userEntity.getId());
        userDto.setRoles(userEntity.getRoles().stream().map(Enum::name).collect(Collectors.toList()));
        userDto.setPhoneNumber(request.getPhoneNumber());

        // Generate JWT Token
        String token = jwtUtil.generateToken(userDto.getId(), userDto.getPhoneNumber(), userDto.getRoles());

        log.info("JWT token generated for user: {}", userDto.getId());
        return token;
    }

    /**
     * Handles user login by calling the authentication method.
     *
     * @param request The login request containing user credentials.
     * @return A JWT token if authentication is successful.
     */
    @Override
    public String login(LoginRequest request) {
        log.debug("Processing login request for phone number: {}", request.getPhoneNumber());
        return authenticateUser(request);
    }
}
