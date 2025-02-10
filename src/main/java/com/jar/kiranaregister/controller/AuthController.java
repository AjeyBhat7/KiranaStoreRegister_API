package com.jar.kiranaregister.controller;

import com.jar.kiranaregister.model.DTOModel.Auth;
import com.jar.kiranaregister.model.DTOModel.LoginResponse;
import com.jar.kiranaregister.service.AuthServices;
import io.micrometer.core.annotation.Timed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
public class AuthController {
    @Autowired
    private AuthServices authServices;

    @Timed(value = "login.api.execution.time", description = "Time taken to execute login API")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody Auth auth) {
        return authServices.logIn(auth);
    }
}