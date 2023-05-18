package com.ownsecurity.controller;

import com.ownsecurity.service.RoleService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/*
POST - add role to user (only for admin) +
GET - get roles (only for admin) +
 */

@RestController
@RequestMapping("/api/roles")
@Api(description = "Отвечает за все операции, связанные с ролями")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @Operation(summary = "Добавить роль пользователю")
    @PostMapping("/{userId}")
    public ResponseEntity addRoleToUserByUserId(@PathVariable @Parameter(description = "Id пользователя") Long userId) throws Exception {
        return ResponseEntity.ok().body(roleService.addRoleToUserByUserId(userId));
    }

    @Operation(summary = "Получить все роли")
    @GetMapping("/")
    public ResponseEntity roles() throws Exception {
        return ResponseEntity.ok().body(roleService.roles());
    }
}
