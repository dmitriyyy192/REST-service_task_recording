package com.ownsecurity.controller;


import com.ownsecurity.dto.UserDto;
import com.ownsecurity.entity.UserEntity;
import com.ownsecurity.exception.TodoIsAlreadyAddToUser;
import com.ownsecurity.exception.TodoNotFoundException;
import com.ownsecurity.exception.UserNotFoundException;
import com.ownsecurity.security.service.UserDetailsImpl;
import com.ownsecurity.service.UserService;
import com.ownsecurity.service.impl.UserServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResourceAccessException;

import java.util.List;


/*
POST - add exist todo to user +
PATH - update user info(username) (for users and admin) +
GET - users (only for admin)+
DELETE - user by user id (only for admin) +
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public ResponseEntity getUsers() throws ResourceAccessException {
        return ResponseEntity.ok().body(userService.getUsers());
    }

    @PostMapping("/{todoId}")
    public ResponseEntity addExistTodoToUser(@PathVariable Long todoId) throws TodoIsAlreadyAddToUser, TodoNotFoundException {
        return ResponseEntity.ok().body(userService.addExistTodoToUser(todoId));
    }

    @PatchMapping("/{userId}")
    public ResponseEntity updateUserInfoByUserId(@PathVariable Long userId, @RequestBody UserEntity user) {
        return ResponseEntity.ok().body(userService.updateUserInfoByUserId(userId, user));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity deleteUserByUserId(@PathVariable Long userId) throws ResourceAccessException, UserNotFoundException {
        return ResponseEntity.ok().body(userService.deleteUserByUserId(userId));
    }
}
