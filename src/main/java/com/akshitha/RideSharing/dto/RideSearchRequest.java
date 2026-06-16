package com.akshitha.RideSharing.dto;

public record RideSearchRequest(
    Double sourceLat,
    Double sourceLng,
    Double destinationLat,
    Double destinationLng
) {}
