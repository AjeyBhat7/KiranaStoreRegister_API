package com.jar.kiranaregister.service;

import com.jar.kiranaregister.model.DTOModel.Auth;
import com.jar.kiranaregister.model.DTOModel.LoginResponse;
import io.micrometer.core.annotation.Timed;
import org.springframework.http.ResponseEntity;

public interface AuthServices {

    @Timed(value = "login.service.execution.time", description = "Time taken to execute logIn service")
    public ResponseEntity<LoginResponse> logIn(Auth auth);
}
