package com.akshitha.RideSharing.dto;

import java.time.LocalDateTime;

import com.akshitha.RideSharing.enums.RideStatus;

public record RideResponse (
    Long rideId,
    Long driverId,
    String driverName,
    Double distanceKm,
    Integer totalPoints,
    LocalDateTime rideTime,
    RideStatus status
){
    
}
