package com.ownsecurity.service;

import com.ownsecurity.entity.RefreshToken;
import com.ownsecurity.payload.request.RefreshTokenRequest;
import com.ownsecurity.payload.response.RefreshTokenResponse;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface RefreshTokenService {

    Optional<RefreshToken> findByToken(String token);

    RefreshToken createRefreshToken(Long userId);

    RefreshToken verifyExpiration(RefreshToken token);

    int deleteByUserId(Long userId);

    ResponseEntity refreshtoken(RefreshTokenRequest refreshTokenRequest);
}
