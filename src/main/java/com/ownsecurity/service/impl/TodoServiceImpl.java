package com.ownsecurity.service.impl;

import com.ownsecurity.dto.TodoDto;
import com.ownsecurity.entity.TodoEntity;
import com.ownsecurity.entity.UserEntity;
import com.ownsecurity.repository.TodoRepository;
import com.ownsecurity.repository.UserRepository;
import com.ownsecurity.security.service.UserDetailsImpl;
import com.ownsecurity.service.TodoService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TodoServiceImpl implements TodoService {

    private final TodoRepository todoRepository;
    private final UserRepository userRepository;

    public TodoServiceImpl(TodoRepository todoRepository, UserRepository userRepository) {
        this.todoRepository = todoRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<TodoDto> todos() throws Exception {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList().contains("ROLE_ADMIN")) {
                return todoRepository.findAll().stream().map(TodoDto::toTodoDto).collect(Collectors.toList());
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new Exception("Ошибка получения задач!");
        }
    }

    @Override
    public List<TodoEntity> todosByUserId(Long userId) throws Exception {
        try {
            return userRepository.findById(userId).get().getTodos();
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public TodoDto createTodo(TodoEntity todo) throws Exception {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserEntity user = userRepository.findById(userDetails.getId()).orElseThrow();

        try {
            List<TodoEntity> userTodos = user.getTodos();
            userTodos.add(todo);
            user.setTodos(userTodos);

            List<UserEntity> todoUsers = new ArrayList<>();
            todoUsers.add(user);
            todo.setUsers(todoUsers);

            return TodoDto.toTodoDto(todoRepository.save(todo));
        } catch (Exception e) {
            throw new Exception("Ошибка добавления задачи!\n" + e.getMessage());
        }
    }

    @Override
    public TodoDto completeTodo(Long todoId) throws Exception {
        Optional<TodoEntity> optTodo = todoRepository.findById(todoId);

        if (optTodo.isPresent()) {
            TodoEntity todo = optTodo.get();
            todo.setCompleted(!todo.getCompleted());
            return TodoDto.toTodoDto(todoRepository.save(todo));
        } else {
            throw new Exception("Задача не найдена!");
        }
    }

    @Override
    public TodoDto removeTodo(Long todoId) throws Exception {
        Optional<TodoEntity> optTodo = todoRepository.findById(todoId);
        if (optTodo.isPresent()) {
            TodoEntity todo = optTodo.get();

            List<Long> usersIdForRemove = new ArrayList<>();
            todo.getUsers().forEach(user -> {
                user.getTodos().remove(todo);
            });

            usersIdForRemove.forEach(userId -> userRepository.deleteById(userId));

            TodoEntity removedTodo = todoRepository.findById(todoId).get();
            todoRepository.deleteById(todoId);

            return TodoDto.toTodoDto(removedTodo);
        } else {
            throw new Exception("Задача не найдена");
        }
    }
}
