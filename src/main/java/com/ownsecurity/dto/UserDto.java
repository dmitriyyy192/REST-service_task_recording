package com.ownsecurity.dto;

import com.ownsecurity.entity.TodoEntity;
import com.ownsecurity.entity.UserEntity;

import java.util.List;
import java.util.stream.Collectors;

public class UserDto {
    private String username;
    private String email;
    private List<TodoDto> todos;
    private List<String> roles;

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<TodoDto> getTodos() {
        return todos;
    }

    public void setTodos(List<TodoDto> todos) {
        this.todos = todos;
    }

    public UserDto(String username, String email, List<TodoDto> todos, List<String> roles) {
        this.username = username;
        this.email = email;
        this.todos = todos;
        this.roles = roles;
    }

    public static UserDto toUserDto(UserEntity user) {
        return new UserDto(user.getUsername(), user.getEmail(), user.getTodos().stream().map(TodoDto::toTodoDto).collect(Collectors.toList()), user.getRoles().stream().map(role -> role.getName().name()).collect(Collectors.toList()));
    }
}
