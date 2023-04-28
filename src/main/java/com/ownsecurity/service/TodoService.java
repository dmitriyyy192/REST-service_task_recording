package com.ownsecurity.service;

import com.ownsecurity.dto.TodoDto;
import com.ownsecurity.entity.TodoEntity;
import com.ownsecurity.exception.TodoNotFoundException;
import com.ownsecurity.exception.UserNotFoundException;
import org.springframework.web.client.ResourceAccessException;

import java.util.List;

public interface TodoService {

    List<TodoDto> todos() throws ResourceAccessException;
    List<TodoDto> todosByUserId(Long userId) throws TodoNotFoundException, UserNotFoundException;
    TodoDto createTodo(TodoEntity todo) throws Exception;
    TodoDto completeTodo(Long todoId) throws Exception;
    TodoDto removeTodo(Long todoId) throws Exception;
}
