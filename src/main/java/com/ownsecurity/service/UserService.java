package com.ownsecurity.service;

import com.ownsecurity.dto.UserDto;
import com.ownsecurity.entity.UserEntity;
import com.ownsecurity.exception.TodoIsAlreadyAddToUser;
import com.ownsecurity.exception.TodoNotFoundException;
import com.ownsecurity.exception.UserNotFoundException;
import org.springframework.web.client.ResourceAccessException;

import java.util.List;

public interface UserService {

    List<UserDto> getUsers() throws ResourceAccessException;

    List updateUserInfoByUserId(Long userId, UserEntity userDetails);

    UserEntity deleteUserByUserId(Long userId) throws ResourceAccessException, UserNotFoundException;

    UserDto addExistTodoToUser(Long todoId) throws ResourceAccessException, TodoNotFoundException, TodoIsAlreadyAddToUser;
}
