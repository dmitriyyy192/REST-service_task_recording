package com.ownsecurity.service.impl;

import com.ownsecurity.dto.UserDto;
import com.ownsecurity.entity.RefreshToken;
import com.ownsecurity.entity.RoleEntity;
import com.ownsecurity.entity.UserEntity;
import com.ownsecurity.entity.enums.ERole;
import com.ownsecurity.payload.request.LoginRequest;
import com.ownsecurity.payload.request.SignupRequest;
import com.ownsecurity.payload.response.JwtResponse;
import com.ownsecurity.repository.RoleRepository;
import com.ownsecurity.repository.UserRepository;
import com.ownsecurity.security.jwt.JwtUtils;
import com.ownsecurity.service.RefreshTokenService;
import com.ownsecurity.security.service.UserDetailsImpl;
import com.ownsecurity.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;


    public AuthServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder encoder, AuthenticationManager authenticationManager, JwtUtils jwtUtils, RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    public UserDto registration(SignupRequest signup) throws Exception {

        if (userRepository.existsByUsername(signup.getUsername())) {
            throw new Exception("Username is already taken!");
        }

        if (userRepository.existsByEmail(signup.getEmail())) {
            throw new Exception("Email is already taken!");
        }

        UserEntity user = new UserEntity(signup.getUsername(), signup.getEmail(), encoder.encode(signup.getPassword()));

        Set<String> strRoles = signup.getRoles();
        Set<RoleEntity> roles = new HashSet<>();

        if (strRoles.isEmpty()) {
            RoleEntity role = roleRepository.findByName(ERole.ROLE_USER).orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(role);
        } else {
            strRoles.forEach(role -> {
                if (role.equals("admin")) {
                    RoleEntity adminRole = roleRepository.findByName(ERole.ROLE_ADMIN).orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                    roles.add(adminRole);
                } else {
                    RoleEntity userRole = roleRepository.findByName(ERole.ROLE_USER).orElseThrow(() -> new RuntimeException("Error: Role is not found"));
                    roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);

        return UserDto.toUserDto(userRepository.save(user));

    }

    @Override
    public JwtResponse authentication(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String accessToken = jwtUtils.generateJwtToken(userDetails);

        List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority()).collect(Collectors.toList());

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

        return new JwtResponse(accessToken, refreshToken.getToken(), userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles);
    }
}
