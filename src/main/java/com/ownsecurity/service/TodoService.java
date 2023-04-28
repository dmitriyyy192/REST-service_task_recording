package com.ownsecurity.service;

import com.ownsecurity.dto.TodoDto;
import com.ownsecurity.entity.TodoEntity;

import java.util.List;

public interface TodoService {

    List<TodoDto> todos() throws Exception;
    List<TodoEntity> todosByUserId(Long userId) throws Exception;
    TodoDto createTodo(TodoEntity todo) throws Exception;
    TodoDto completeTodo(Long todoId) throws Exception;
    TodoDto removeTodo(Long todoId) throws Exception;
}
