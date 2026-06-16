package com.akshitha.RideSharing.mapper;

import org.springframework.stereotype.Component;

import com.akshitha.RideSharing.dto.CreateUser;
import com.akshitha.RideSharing.dto.UserResponse;
import com.akshitha.RideSharing.entity.User;

@Component
public class UserMapper {
    public User mapToUser(CreateUser request){
        return User.builder()
                    .name(request.name())
                    .phone(request.phone())
                    .role(request.role())
                    .build();
    }
    public UserResponse mapToUserResponse(User user){
        return new UserResponse(user.getId(),user.getName(),user.getPhone(),user.getRole());
    }
}
