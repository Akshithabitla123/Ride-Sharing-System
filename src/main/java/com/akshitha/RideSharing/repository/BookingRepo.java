package com.akshitha.RideSharing.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.akshitha.RideSharing.entity.Booking;

public interface BookingRepo extends JpaRepository<Booking,Long>{
    List<Booking> findByRiderId(Long riderId);
    List<Booking> findByRideDriverId(Long driverId);
    List<Booking> findByRideIdAndRiderId(Long rideId,Long riderId);
}
