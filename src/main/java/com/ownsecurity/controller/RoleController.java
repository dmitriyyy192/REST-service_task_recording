package com.ownsecurity.controller;

import com.ownsecurity.dto.UserDto;
import com.ownsecurity.service.RoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/*
POST - add role to user (only for admin) +
GET - get roles (only for admin) +
 */

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/{userId}")
    public ResponseEntity addRoleToUserByUserId(@PathVariable Long userId) throws Exception {
        UserDto user = roleService.addRoleToUserByUserId(userId);
        return user == null ? ResponseEntity.status(HttpStatus.FORBIDDEN).body("У вас нет прав доступа к данному ресурсу!")
                : ResponseEntity.ok().body(user);
    }

    @GetMapping("/")
    public ResponseEntity roles() throws Exception {
        return ResponseEntity.ok().body(roleService.roles());
    }
}
