package com.digitalevidence.mvc.mongodb.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.digitalevidence.mvc.mongodb.dto.common.ApiMessageResponse;
import com.digitalevidence.mvc.mongodb.dto.common.LoginResponse;
import com.digitalevidence.mvc.mongodb.dto.user.LoginRequest;
import com.digitalevidence.mvc.mongodb.dto.user.RegisterUserRequest;
import com.digitalevidence.mvc.mongodb.dto.user.UserResponse;
import com.digitalevidence.mvc.mongodb.service.UserService;

import jakarta.validation.Valid;

@RestController
@Profile("mongo")
@RequestMapping("/api/mongo/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse registerUser(@Valid @RequestBody RegisterUserRequest request) {
        return userService.register(request);
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return userService.login(request);
    }

    @GetMapping
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    @DeleteMapping("/{id}")
    public ApiMessageResponse deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return new ApiMessageResponse("User deleted successfully");
    }
}
