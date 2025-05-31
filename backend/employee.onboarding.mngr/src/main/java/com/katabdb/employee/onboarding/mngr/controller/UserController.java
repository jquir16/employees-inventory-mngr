package com.katabdb.employee.onboarding.mngr.controller;

import com.katabdb.employee.onboarding.mngr.domain.UserEntity;
import com.katabdb.employee.onboarding.mngr.services.implementation.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user")
    public UserEntity getUserById(@RequestParam Integer id) {
        return userService.getUserById(id);
    }
}
