package com.akshitha.RideSharing.dto;

import java.time.LocalDateTime;

public record RideResponse (
    Long rideId,
    Long driverId,
    String driverName,
    Double distanceKm,
    Integer totalPoints,
    LocalDateTime rideTime
){
    
}
