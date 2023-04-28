package com.ownsecurity.service;

import com.ownsecurity.dto.UserDto;
import com.ownsecurity.entity.UserEntity;
import com.ownsecurity.security.service.UserDetailsImpl;

import java.util.List;

public interface UserService {

    List<UserDto> getUsers() throws Exception;

    List updateUserInfoByUserId(Long userId, UserEntity userDetails) throws Exception;

    UserEntity deleteUserByUserId(Long userId) throws Exception;

    UserDto addExistTodoToUser(Long todoId) throws Exception;
}
