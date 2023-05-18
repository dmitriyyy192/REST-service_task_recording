package com.ownsecurity.service.impl;

import com.ownsecurity.dto.TodoDto;
import com.ownsecurity.entity.TodoEntity;
import com.ownsecurity.entity.UserEntity;
import com.ownsecurity.exception.TodoNotFoundException;
import com.ownsecurity.exception.UserNotFoundException;
import com.ownsecurity.repository.TodoRepository;
import com.ownsecurity.repository.UserRepository;
import com.ownsecurity.security.service.UserDetailsImpl;
import com.ownsecurity.service.TodoService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

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
    public List<TodoDto> todos() throws ResourceAccessException {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList().contains("ROLE_ADMIN")) {
            return todoRepository.findAll().stream().map(TodoDto::toTodoDto).collect(Collectors.toList());
        } else {
            throw new ResourceAccessException("У вас нет права доступа к данному ресурсу!");
        }

    }

    @Override
    public List<TodoDto> todosByUserId(Long userId) throws TodoNotFoundException, UserNotFoundException {
        Optional<UserEntity> user = userRepository.findById(userId);
        if (user.isPresent()) {
            List<TodoDto> todoDos = user.get().getTodos().stream().map(TodoDto::toTodoDto).toList();
            if (todoDos == null) {
                throw new TodoNotFoundException("Задачи не найдены!");
            } else {
                return todoDos;
            }
        } else {
            throw new UserNotFoundException("Пользователь с id = " + userId + " не найден!");
        }

    }

    @Override
    public TodoDto createTodo(TodoEntity todo) throws Exception {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        UserEntity user = userRepository.findById(userDetails.getId()).get();
        if (user == null) {
            throw new UserNotFoundException("Пользователь с id = " + userDetails.getId() + " не найден!");
        }

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
    public TodoDto updateTodo(Long todoId, TodoEntity changedTodo) {
        TodoEntity todo = todoRepository.findById(todoId).orElseThrow(new TodoNotFoundException("Задача не найдена!"));

        if (!(changedTodo.getTitle() == null)) {
            todo.setTitle(changedTodo.getTitle());
        }
        if (!(changedTodo.getCompleted() == null)) {
            todo.setCompleted(!todo.getCompleted());
        }

        return TodoDto.toTodoDto(todoRepository.save(todo));

//        Optional<TodoEntity> optTodo = todoRepository.findById(todoId);
//
//        if (optTodo.isPresent()) {
//            TodoEntity todo = optTodo.get();
//            todo.setCompleted(!todo.getCompleted());
//            return TodoDto.toTodoDto(todoRepository.save(todo));
//        } else {
//            throw new TodoNotFoundException("Задача не найдена!");
//        }
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
            throw new TodoNotFoundException("Задача не найдена");
        }
    }
}
