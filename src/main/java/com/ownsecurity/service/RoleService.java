package com.ownsecurity.service;

import com.ownsecurity.dto.UserDto;
import com.ownsecurity.entity.RoleEntity;

import java.util.Set;

public interface RoleService {

    UserDto addRoleToUserByUserId(Long userId) throws Exception;

    Set<RoleEntity> roles() throws Exception;
}
