package com.ownsecurity.controller;


import com.ownsecurity.dto.TodoDto;
import com.ownsecurity.entity.TodoEntity;
import com.ownsecurity.service.TodoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
GET - todos, todos by id user +
POST - add todo +
PATH - complete todo +
DELETE - delete todo by todo id +
 */

@RestController
@RequestMapping("/api/todos")
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping("/")
    public ResponseEntity todos() throws Exception {
        List<TodoDto> todos = todoService.todos();
        return todos == null ? ResponseEntity.status(HttpStatus.FORBIDDEN).body("У вас нет прав доступа к этому ресурсу!")
                : ResponseEntity.ok().body(todos);
//        List<TodoEntity> todos = todoService.todos();
//        if (todos == null) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("У вас нет прав доступа к этому ресурсу!");
//        }
//        return ResponseEntity.ok().body(todoService.todos());
    }

    @GetMapping("/{userId}")
    public ResponseEntity todosByUserId(@PathVariable Long userId) throws Exception {
        return ResponseEntity.ok().body(todoService.todosByUserId(userId));
    }

    @PostMapping("/")
    public ResponseEntity createTodo(@RequestBody TodoEntity todo) throws Exception {
        return ResponseEntity.ok().body(todoService.createTodo(todo));
    }

    @PatchMapping("/{todoId}")
    public ResponseEntity completeTodo(@PathVariable Long todoId) throws Exception {
        return ResponseEntity.ok().body(todoService.completeTodo(todoId));
    }

    @DeleteMapping("/{todoId}")
    public ResponseEntity removeTodo(@PathVariable Long todoId) throws Exception {
        return ResponseEntity.ok().body(todoService.removeTodo(todoId));
    }
}
