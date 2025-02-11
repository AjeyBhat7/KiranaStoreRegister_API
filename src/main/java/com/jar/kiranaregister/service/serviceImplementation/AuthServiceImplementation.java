package com.jar.kiranaregister.service.serviceImplementation;

import com.jar.kiranaregister.model.DTOModel.UserDto;
import com.jar.kiranaregister.model.entity.UserEntity;
import com.jar.kiranaregister.model.requestObj.LoginRequest;
import com.jar.kiranaregister.repository.UserRepository;
import com.jar.kiranaregister.service.AuthService;
import com.jar.kiranaregister.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Override
    public String authenticateUser(LoginRequest request) {
        UserEntity user = userRepository.findByPhoneNumber(request.getPhoneNumber()).orElse(null);
        String userId = Optional.ofNullable(user).map(UserEntity::getId).orElse(null);
        if (userId == null) {
            throw new UsernameNotFoundException("User not found for the phone number");
        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userId, request.getPassword()));

        UserEntity userEntity = userRepository.findByPhoneNumber(request.getPhoneNumber())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        UserDto userDto = new UserDto();

        userDto.setId(userEntity.getId());
        userDto.setRoles(userEntity.getRoles().stream().map(Enum::name).collect(Collectors.toList()));
        userDto.setPhoneNumber(request.getPhoneNumber());

        return jwtUtil.generateToken(userDto.getId(), userDto.getPhoneNumber(),userDto.getRoles());
    }

    @Override
    public String login(LoginRequest request) {
        return authenticateUser(request);
    }
}