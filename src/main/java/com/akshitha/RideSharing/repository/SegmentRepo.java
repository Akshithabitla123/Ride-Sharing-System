package com.akshitha.RideSharing.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.akshitha.RideSharing.entity.SegmentInventory;

public interface SegmentRepo extends JpaRepository<SegmentInventory,Long>{
    List<SegmentInventory> findByRideIdOrderByFromIndex(Long rideId);
    List<SegmentInventory> findByRideId(Long rideId);
}
