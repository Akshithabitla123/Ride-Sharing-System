package com.akshitha.RideSharing.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.akshitha.RideSharing.entity.RoutePoint;

public interface RoutePointRepo extends JpaRepository<RoutePoint,Long>{
    public int countByRideId(Long rideId);
    public List<RoutePoint> findByRideIdOrderBySequenceNo(Long rideid);
}
