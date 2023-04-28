package com.ownsecurity.service;

import com.ownsecurity.entity.UserEntity;
import com.ownsecurity.payload.request.LoginRequest;
import com.ownsecurity.payload.request.SignupRequest;
import com.ownsecurity.payload.response.JwtResponse;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    public UserEntity registration(SignupRequest signup) throws Exception;
    public JwtResponse authentication(LoginRequest loginRequest);
}
