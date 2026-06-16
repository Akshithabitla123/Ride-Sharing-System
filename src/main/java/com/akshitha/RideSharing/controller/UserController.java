package com.akshitha.RideSharing.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.akshitha.RideSharing.dto.CreateUser;
import com.akshitha.RideSharing.dto.UserResponse;
import com.akshitha.RideSharing.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name="USER",description="User Management APIs")
public class UserController {
    private final UserService userService;

    @Operation(summary="Create user")
    @PostMapping
    public UserResponse createUser(@RequestBody CreateUser request){
        System.out.println(request);
        return userService.createUser(request);
    }

    @Operation(summary="Get user by Id")
    @GetMapping("/{id}")
    public UserResponse getUser(@PathVariable Long id){
        return userService.getUser(id);
    }
    @Operation(summary="Get all users")
    @GetMapping
    public List<UserResponse> getAllUsers(){
        return userService.getAllUsers();
    }
}
