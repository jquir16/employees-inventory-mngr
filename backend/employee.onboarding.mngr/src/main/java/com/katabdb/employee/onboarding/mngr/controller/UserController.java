package com.katabdb.employee.onboarding.mngr.controller;

import com.katabdb.employee.onboarding.mngr.domain.entities.UserEntity;
import com.katabdb.employee.onboarding.mngr.services.implementation.UserService;
import com.katabdb.employee.onboarding.mngr.services.interfaces.IUserQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    private final IUserQueryService userQueryService;

    @Autowired
    public UserController(UserService userService) {
        this.userQueryService = userService;
    }

    @GetMapping("/{id}")
    public UserEntity getById(@Validated @PathVariable Integer id) {
        return userQueryService.getUserById(id);
    }

    @GetMapping
    public List<UserEntity> getAll() {
        return userQueryService.getAllUsers();
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserEntity> updateUser(
            @PathVariable Integer id,
            @RequestBody @Validated UserEntity userUpdate) {
        UserEntity updated = userQueryService.updateUser(id, userUpdate);
        return ResponseEntity.ok(updated);
    }
}
