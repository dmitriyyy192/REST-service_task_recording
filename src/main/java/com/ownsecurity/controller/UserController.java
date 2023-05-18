package com.ownsecurity.controller;


import com.ownsecurity.entity.UserEntity;
import com.ownsecurity.exception.TodoIsAlreadyAddToUser;
import com.ownsecurity.exception.TodoNotFoundException;
import com.ownsecurity.exception.UserNotFoundException;
import com.ownsecurity.service.UserService;
import com.ownsecurity.service.impl.UserServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResourceAccessException;

/*
POST - add exist todo to user +
PATH - update user info(username) (for users and admin) +
GET - users (only for admin)+
DELETE - user by user id (only for admin) +
 */
@RestController
@RequestMapping("/api/users")
@Api(description = "Отвечает за все операции, связанные с пользователем")
public class UserController {

    private final UserService userService;

    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @Operation(summary = "Получить всех пользователей")
    @GetMapping("/")
    public ResponseEntity getUsers() throws ResourceAccessException {
        return ResponseEntity.ok().body(userService.getUsers());
    }

    @Operation(summary = "Добавить существующую задачу пользователю")
    @PostMapping("/{todoId}")
    public ResponseEntity addExistTodoToUser(@PathVariable @Parameter(description = "Id задачи") Long todoId) throws TodoIsAlreadyAddToUser, TodoNotFoundException {
        return ResponseEntity.ok().body(userService.addExistTodoToUser(todoId));
    }

    @Operation(summary = "Обновить информацию о пользователе")
    @PatchMapping("/{userId}")
    public ResponseEntity updateUserInfoByUserId(@PathVariable @Parameter(description = "Id пользователя, которого нужно обновить") Long userId,
                                                 @RequestBody @Parameter(description = "Поля, которые нужно обновить") UserEntity user) {
        return ResponseEntity.ok().body(userService.updateUserInfoByUserId(userId, user));
    }

    @Operation(summary = "Удалить пользователя по id")
    @DeleteMapping("/{userId}")
    public ResponseEntity deleteUserByUserId(@PathVariable @Parameter(description = "Id пользователя") Long userId) throws ResourceAccessException, UserNotFoundException {
        return ResponseEntity.ok().body(userService.deleteUserByUserId(userId));
    }
}
