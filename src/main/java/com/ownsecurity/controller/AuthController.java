package com.ownsecurity.controller;

import com.ownsecurity.payload.request.*;
import com.ownsecurity.service.RefreshTokenService;
import com.ownsecurity.service.impl.AuthServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/*
POST - signup user
POST - signin user
POST - get new access token with refreshtoken
 */

@RestController
@RequestMapping("/api/auth")
@Api(description = "Отвечает за все операции, связанные с авторизацией")
public class AuthController {

    private final AuthServiceImpl authService;
    private final RefreshTokenService refreshTokenService;


    public AuthController(AuthServiceImpl authService, RefreshTokenService refreshTokenService) {
        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
    }

    @Operation(summary = "Вход пользователя")
    @PostMapping("/signin")
    public ResponseEntity authenticateUser(@Valid @RequestBody @Parameter(description = "Поля, необходимые для входа пользователя") LoginRequest loginRequest) {
        return ResponseEntity.ok().body(authService.authentication(loginRequest));
    }

    @Operation(summary = "Позволяет получить новый access token")
    @PostMapping("/refreshtoken")
    public ResponseEntity refreshtoken(@Valid @RequestBody @Parameter(description = "Refresh токен") RefreshTokenRequest request) {
        return ResponseEntity.ok().body(refreshTokenService.refreshtoken(request));
    }

    @Operation(summary = "Регистрация пользователя")
    @PostMapping("/signup")
    public ResponseEntity registerUser(@Valid @RequestBody @Parameter(description = "Поля, необходимые для регистрации пользователя") SignupRequest signup) throws Exception {
        return ResponseEntity.ok().body(authService.registration(signup));
    }
}
