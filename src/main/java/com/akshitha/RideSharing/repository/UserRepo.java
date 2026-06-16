package com.akshitha.RideSharing.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.akshitha.RideSharing.entity.User;

public interface UserRepo extends JpaRepository<User, Long>{
    boolean existsByPhone(String phone);
}
