package com.akshitha.RideSharing.dto;

import com.akshitha.RideSharing.enums.Role;

public record  UserResponse (
    Long id,
    String name,
    String phone,
    Role role
){
    
}
