package com.ownsecurity.service.impl;

import com.ownsecurity.dto.UserDto;
import com.ownsecurity.entity.RoleEntity;
import com.ownsecurity.entity.UserEntity;
import com.ownsecurity.entity.enums.ERole;
import com.ownsecurity.repository.RoleRepository;
import com.ownsecurity.repository.UserRepository;
import com.ownsecurity.security.service.UserDetailsImpl;
import com.ownsecurity.service.RoleService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;


    public RoleServiceImpl(RoleRepository roleRepository, UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    @Override
    public UserDto addRoleToUserByUserId(Long userId) throws Exception {

        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(userDetails.getAuthorities().stream().map(authority -> authority.getAuthority()).collect(Collectors.toList()).contains("ROLE_ADMIN")) {

            Optional<UserEntity> optUser = userRepository.findById(userId);

            if(optUser.isPresent()) {
                UserEntity user = optUser.get();
                Set<RoleEntity> userRoles = user.getRoles();
                userRoles.add(roleRepository.findByName(ERole.ROLE_ADMIN).get());
                user.setRoles(userRoles);
                return UserDto.toUserDto(userRepository.save(user));
            } else {
                throw new Exception("Не удалось найти пользователя!");
            }
        } else {
            return null;
        }
    }

    @Override
    public Set<RoleEntity> roles() throws Exception {

        UserDetailsImpl user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList().contains("ROLE_ADMIN")) {
            return new HashSet<>(roleRepository.findAll());
        } else {
            throw new Exception("You do not have access rights to this functionality.");
        }
    }

}
