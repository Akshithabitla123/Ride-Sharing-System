package com.akshitha.RideSharing.dto;

import java.time.LocalDateTime;

import com.akshitha.RideSharing.enums.BookingStatus;

public record BookingResponse(
    Long bookingId,
    Long rideId,
    String driverName,
    String driverPhoneNo,
    Coordinate pickUpPoint,
    Coordinate dropPoint,
    LocalDateTime rideDateTime,
    Integer seatsBooked,
    Double distanceKm,
    Double fare,
    BookingStatus status
) {}
