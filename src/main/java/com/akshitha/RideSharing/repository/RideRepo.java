package com.akshitha.RideSharing.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.akshitha.RideSharing.entity.Ride;

public interface RideRepo extends JpaRepository<Ride, Long>{
    
}
