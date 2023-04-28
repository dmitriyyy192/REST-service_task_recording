package com.ownsecurity.service.impl;

import com.ownsecurity.dto.UserDto;
import com.ownsecurity.entity.TodoEntity;
import com.ownsecurity.entity.UserEntity;
import com.ownsecurity.repository.TodoRepository;
import com.ownsecurity.repository.UserRepository;
import com.ownsecurity.security.jwt.JwtUtils;
import com.ownsecurity.security.service.UserDetailsImpl;
import com.ownsecurity.service.UserService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TodoRepository todoRepository;
    private final JwtUtils jwtUtils;


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
            return null;
        }
    }

    @Override
    public List updateUserInfoByUserId(Long userId, UserEntity changedUser) throws Exception {

        UserEntity user = userRepository.findById(userId).orElseThrow();

//        try {
//            Field[] userFields = user.getClass().getDeclaredFields();
//
//            Arrays.stream(userFields).forEach(userField -> {
//                userField.setAccessible(true);
//                Object value;
//                try {
//                    value = userField.get(changedUser);
//                    if (value != null) {
//                        userField.set(user, value);
//                    }
//                } catch (IllegalAccessException e) {
//                    throw new RuntimeException(e.getMessage());
//                }
//            });
//        } catch (Exception e) {
//            throw new Exception(e.getMessage());
//        }
//
//        List lst = new ArrayList<>();
//        lst.add(UserDto.toUserDto(userRepository.save(user)));
//        lst.add(jwtUtils.generateJwtToken(new UserDetailsImpl(user.getId(),user.getUsername(),user.getEmail(),user.getPassword(),user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList()))));
//
//        return lst;
//
        if (!(changedUser.getUsername() == null)) {
            user.setUsername(changedUser.getUsername());
        }

        if (!(changedUser.getEmail() == null)) {
            user.setEmail(changedUser.getEmail());
        }

        if (!(changedUser.getPassword() == null)) {
            user.setPassword(passwordEncoder.encode(changedUser.getPassword()));
        }

        List lst = new ArrayList<>();
        lst.add(UserDto.toUserDto(userRepository.save(user)));
        lst.add(jwtUtils.generateJwtToken(new UserDetailsImpl(user.getId(),user.getUsername(),user.getEmail(),user.getPassword(),user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList()))));

        return lst;
    }

    @Override
    public UserEntity deleteUserByUserId(Long userId) throws Exception {

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
                throw new Exception("Пользователь с id = " + userId + " не найден!");
            }
        } else {
            return null;
        }
    }

    @Override
    public UserDto addExistTodoToUser(Long todoId) throws Exception {

        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<TodoEntity> optTodo = todoRepository.findById(todoId);

        if(optTodo.isPresent()) {
            TodoEntity todo = optTodo.get();
            UserEntity user = userRepository.findById(userDetails.getId()).get();

            List<TodoEntity> userTodos = user.getTodos();
            if(userTodos.contains(todo)) {
                throw new Exception("Эта задача уже есть в списке данного пользователя!");
            } else {
                List<UserEntity> todoUsers = todo.getUsers();

                userTodos.add(todo);
                user.setTodos(userTodos);

                todoUsers.add(user);
                todo.setUsers(todoUsers);

                return UserDto.toUserDto(userRepository.save(user));
            }
        } else {
            throw new Exception("Невозможно добавить задачу!");
        }
    }
}
