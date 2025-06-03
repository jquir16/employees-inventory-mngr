package com.katabdb.employee.onboarding.mngr.controller;

import com.katabdb.employee.onboarding.mngr.domain.entities.UserEntity;
import com.katabdb.employee.onboarding.mngr.dto.user.UserResponse;
import com.katabdb.employee.onboarding.mngr.dto.user.UserUpdateRequest;
import com.katabdb.employee.onboarding.mngr.services.implementation.UserService;
import com.katabdb.employee.onboarding.mngr.services.spec.IUserQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final IUserQueryService userQueryService;

    @Autowired
    public UserController(UserService userService) {
        this.userQueryService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(@Validated @PathVariable Integer id) {
        return ResponseEntity.ok(userQueryService.getUserById(id));
    }

    @GetMapping
    public List<UserResponse> getAll() {
        return userQueryService.getAllUsers();
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Integer id,
            @RequestBody @Validated UserUpdateRequest userUpdate) {
        UserResponse updated = userQueryService.updateUser(id, userUpdate);
        return ResponseEntity.ok(updated);
    }
}
