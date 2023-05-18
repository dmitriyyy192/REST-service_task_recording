package com.ownsecurity.controller;


import com.ownsecurity.entity.TodoEntity;
import com.ownsecurity.exception.TodoNotFoundException;
import com.ownsecurity.exception.UserNotFoundException;
import com.ownsecurity.service.TodoService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResourceAccessException;


/*
GET - todos, todos by userId +
POST - add todo +
PATH - update todo +
DELETE - delete todo by todo id +
 */

@RestController
@RequestMapping("/api/todos")
@Api(description = "Отвечает за все операции, связанные с задачами пользователей")
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @Operation(summary = "Получить все задачи")
    @GetMapping("/")
    public ResponseEntity todos() throws ResourceAccessException {
        return ResponseEntity.ok().body(todoService.todos());
    }

    @Operation(summary = "Получить все задачи конкретного пользователя (по id)")
    @GetMapping("/{userId}")
    public ResponseEntity todosByUserId(@PathVariable @Parameter(description = "Id пользователя") Long userId) throws TodoNotFoundException, UserNotFoundException {
        return ResponseEntity.ok().body(todoService.todosByUserId(userId));
    }

    @Operation(summary = "Создать задачу")
    @PostMapping("/")
    public ResponseEntity createTodo(@RequestBody @Parameter(description = "Тело задачи в формате Json") TodoEntity todo) throws Exception {
        return ResponseEntity.ok().body(todoService.createTodo(todo));
    }

    @Operation(summary = "Обновить информацию о задаче")
    @PatchMapping("/{todoId}")
    public ResponseEntity updateTodo(@PathVariable @Parameter(description = "Id задачи") Long todoId,
                                     @RequestBody @Parameter(description = "Поля которые нужно обновить") TodoEntity todo) {
        return ResponseEntity.ok().body(todoService.updateTodo(todoId, todo));
    }

    @Operation(summary = "Удалить задачу")
    @DeleteMapping("/{todoId}")
    public ResponseEntity removeTodo(@PathVariable @Parameter(description = "Id задачи") Long todoId) throws Exception {
        return ResponseEntity.ok().body(todoService.removeTodo(todoId));
    }
}
