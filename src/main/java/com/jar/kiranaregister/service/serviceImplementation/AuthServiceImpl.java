package com.jar.kiranaregister.service.serviceImplementation;

import com.jar.kiranaregister.model.DTOModel.Auth;
import com.jar.kiranaregister.model.DTOModel.LoginResponse;
import com.jar.kiranaregister.model.User;
import com.jar.kiranaregister.repository.RoleRepo;
import com.jar.kiranaregister.repository.UserRepository;
import com.jar.kiranaregister.service.AuthServices;
import com.jar.kiranaregister.utils.JwtUtil;

import com.jar.kiranaregister.repository.AuthRepo;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthServiceImpl implements AuthServices {

    private final BCryptPasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtUtil jwtUtil;

    private final CustomUserDetailsService userDetailsService;


    private final UserRepository userRepo;


    @Autowired
    public AuthServiceImpl(BCryptPasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtUtil jwtUtil, CustomUserDetailsService userDetailsService, UserRepository userRepo) {
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.userRepo = userRepo;
    }

    @Timed(value = "login.service.execution.time", description = "Time taken to execute logIn service")
    @Override
    public ResponseEntity<LoginResponse> logIn(Auth auth) {

        long startTime = System.currentTimeMillis();

        try {

            // Fetch user from database
            User user = userRepo.findByUserName(auth.getUsername());

            if (user == null) {
                return new ResponseEntity<>(
                        new LoginResponse("User not found", false),
                        HttpStatus.NOT_FOUND
                );
            }

            // Authenticate user
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(auth.getUsername(), auth.getPassword())
            );

            // Load user details
            UserDetails userDetails = userDetailsService.loadUserByUsername(auth.getUsername());


            // Generate JWT token
            String token = jwtUtil.generateToken(userDetails);
            long userId = user.getId();

            // Check if the user is an admin
            boolean isAdmin = user.getRole().equals("ADMIN");

            return ResponseEntity.ok(new LoginResponse(token, "Login successful", userId, true, isAdmin));

        } catch (BadCredentialsException e) {
            log.error("Invalid credentials for user: {}", auth.getUsername());
            return new ResponseEntity<>(new LoginResponse("Invalid username or password", false), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            log.error("An error occurred during login: {}", e.getMessage());
            return new ResponseEntity<>(new LoginResponse(e.getMessage(), false), HttpStatus.BAD_REQUEST);
        }
    }


}
