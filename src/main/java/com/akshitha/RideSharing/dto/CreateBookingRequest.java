package com.akshitha.RideSharing.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateBookingRequest(
    @NotNull Long rideId,
    @NotNull Long riderId,
    @NotNull Double pickupLat,
    @NotNull Double pickupLng,
    @NotNull Double dropLat,
    @NotNull Double dropLng,
    @NotNull @Min(1) Integer seatsBooked
) {}
