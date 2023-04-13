package com.ownsecurity.service.impl;

import com.ownsecurity.entity.RoleEntity;
import com.ownsecurity.entity.UserEntity;
import com.ownsecurity.entity.enums.ERole;
import com.ownsecurity.payload.request.SignupRequest;
import com.ownsecurity.repository.RoleRepository;
import com.ownsecurity.repository.UserRepository;
import com.ownsecurity.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;

    public AuthServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
    }

    @Override
    public ResponseEntity registration(SignupRequest signup) {

        if(userRepository.existsByUsername(signup.getUsername())) {
            return ResponseEntity.badRequest().body("Username is already taken!");
        }

        if(userRepository.existsByEmail(signup.getEmail())) {
            return ResponseEntity.badRequest().body("Email is already taken!");
        }

        UserEntity user = new UserEntity(signup.getUsername(), signup.getEmail(), encoder.encode(signup.getPassword()));

        List<String> strRoles = signup.getRoles();
        List<RoleEntity> roles = new ArrayList<>();

        if(strRoles == null) {
            RoleEntity role = roleRepository.findByName(ERole.ROLE_USER).orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(role);
        } else {
            strRoles.forEach(role -> {
                if(role == "admin") {
                    RoleEntity adminRole = roleRepository.findByName(ERole.ROLE_ADMIN).orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                    roles.add(adminRole);
                } else {
                    RoleEntity userRole = roleRepository.findByName(ERole.ROLE_USER).orElseThrow(() -> new RuntimeException("Error: Role is not found"));
                    roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        System.out.println("its working");
        return ResponseEntity.ok().body("User registered successfully!" + "\n" + userRepository.save(user));

    }
}
