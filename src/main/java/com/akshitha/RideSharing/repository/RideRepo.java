package com.akshitha.RideSharing.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.akshitha.RideSharing.entity.Ride;
import com.akshitha.RideSharing.enums.RideStatus;


public interface RideRepo extends JpaRepository<Ride, Long>{
    List<Ride> findByStatus(RideStatus status);
}
