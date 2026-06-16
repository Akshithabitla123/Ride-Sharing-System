package com.akshitha.RideSharing.dto;

import java.time.LocalDateTime;

public record RideSearchResponse(
    Long rideId,
    String driverName,
   Coordinate pickupPoint,
    Coordinate dropPoint,
    LocalDateTime rideDateTime,
    Double pickUpDistanceMeters,
    Double dropDistanceMeters
) {
    
}
