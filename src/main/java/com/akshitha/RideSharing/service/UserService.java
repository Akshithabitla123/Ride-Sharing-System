package com.akshitha.RideSharing.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.akshitha.RideSharing.dto.CreateUser;
import com.akshitha.RideSharing.dto.UserResponse;
import com.akshitha.RideSharing.entity.User;
import com.akshitha.RideSharing.exceptions.BadRequestException;
import com.akshitha.RideSharing.exceptions.ResourceNotFoundException;
import com.akshitha.RideSharing.mapper.UserMapper;
import com.akshitha.RideSharing.repository.UserRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepo userRepo;
    private final UserMapper userMapper;
    public UserResponse createUser(CreateUser request){
        if(userRepo.existsByPhone(request.phone())){
            throw new BadRequestException("Phone number already exists");
        }
        User user=userMapper.mapToUser(request);
        User savedUser=userRepo.save(user);
        return userMapper.mapToUserResponse(savedUser);
    }
    public UserResponse getUser(Long id){
        User user=userRepo.findById(id).orElseThrow(()->new ResourceNotFoundException("User not found with given id"));
        return userMapper.mapToUserResponse(user);
    }
    public List<UserResponse> getAllUsers(){
        return userRepo.findAll().stream().map(userMapper::mapToUserResponse).toList();
    }

}
