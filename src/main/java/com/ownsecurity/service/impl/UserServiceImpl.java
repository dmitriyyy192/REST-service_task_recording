package com.ownsecurity.service.impl;

import com.ownsecurity.dto.UserDto;
import com.ownsecurity.entity.TodoEntity;
import com.ownsecurity.entity.UserEntity;
import com.ownsecurity.exception.TodoIsAlreadyAddToUser;
import com.ownsecurity.exception.TodoNotFoundException;
import com.ownsecurity.exception.UserNotFoundException;
import com.ownsecurity.repository.TodoRepository;
import com.ownsecurity.repository.UserRepository;
import com.ownsecurity.security.jwt.JwtUtils;
import com.ownsecurity.security.service.UserDetailsImpl;
import com.ownsecurity.service.UserService;
import com.ownsecurity.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TodoRepository todoRepository;
    private final JwtUtils jwtUtils;
    @Autowired
    Utils utils;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, TodoRepository todoRepository, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.todoRepository = todoRepository;
        this.jwtUtils = jwtUtils;
    }

    @Override
    public List<UserDto> getUsers() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList().contains("ROLE_ADMIN")) {
            return userRepository.findAll().stream().map(UserDto::toUserDto).collect(Collectors.toList());
        } else {
            throw new ResourceAccessException("У вас нет доступа к данному ресурсу!");
        }
    }

    @Override
    public List updateUserInfoByUserId(Long userId, UserEntity changedUser) {

        UserEntity user = userRepository.findById(userId).orElseThrow(new UserNotFoundException("Пользователь не найден!"));

        utils.changeFields(user, changedUser);

        List lst = new ArrayList<>();
        lst.add(UserDto.toUserDto(userRepository.save(user)));
        lst.add(jwtUtils.generateJwtToken(new UserDetailsImpl(user.getId(),user.getUsername(),user.getEmail(),user.getPassword(),user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList()))));

        return lst;
    }

    @Override
    public UserEntity deleteUserByUserId(Long userId) throws ResourceAccessException, UserNotFoundException {

        UserDetailsImpl securityUser = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (securityUser.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList().contains("ROLE_ADMIN")) {

            Optional<UserEntity> optUser = userRepository.findById(userId);

            if (optUser.isPresent()) {
                UserEntity user = optUser.get();
                List<TodoEntity> todosIdForRemove = new ArrayList<>();
                user.getTodos().forEach(todo -> {
                    todo.getUsers().remove(user);
                    todoRepository.save(todo);
                    if (todo.getUsers().size() == 0) {
                        todosIdForRemove.add(todo);
                    }
                });

                todoRepository.deleteAll(todosIdForRemove);

                UserEntity removedUser = userRepository.findById(userId).get();
                userRepository.deleteById(userId);

                return removedUser;
            } else {
                throw new UserNotFoundException("Пользователь с id = " + userId + " не найден!");
            }
        } else {
            throw new ResourceAccessException("У вас нет доступа к данному ресурсу!");
        }
    }

    @Override
    public UserDto addExistTodoToUser(Long todoId) throws TodoNotFoundException, TodoIsAlreadyAddToUser {

        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<TodoEntity> optTodo = todoRepository.findById(todoId);

        if(optTodo.isPresent()) {
            TodoEntity todo = optTodo.get();
            UserEntity user = userRepository.findById(userDetails.getId()).get();

            List<TodoEntity> userTodos = user.getTodos();
            if(userTodos.contains(todo)) {
                throw new TodoIsAlreadyAddToUser("Эта задача уже есть у данного пользователя!");
            } else {
                List<UserEntity> todoUsers = todo.getUsers();

                userTodos.add(todo);
                user.setTodos(userTodos);

                todoUsers.add(user);
                todo.setUsers(todoUsers);

                return UserDto.toUserDto(userRepository.save(user));
            }
        } else {
            throw new TodoNotFoundException("Задача не найдена!");
        }
    }
}
