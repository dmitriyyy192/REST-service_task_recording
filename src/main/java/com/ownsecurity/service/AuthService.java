package com.ownsecurity.service;

import com.ownsecurity.payload.request.SignupRequest;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    public ResponseEntity registration(SignupRequest signup);
}
