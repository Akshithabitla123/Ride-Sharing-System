package com.akshitha.RideSharing.dto;

public record SeatMapResponse(
    Integer fromIndex,Integer toIndex, Integer availableSeats
) {}
