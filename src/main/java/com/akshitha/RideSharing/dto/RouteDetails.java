package com.akshitha.RideSharing.dto;

import java.util.List;

public record RouteDetails(
    double distanceKm,
    List<List<Double>> coordinates
) {
    
}
