package com.akshitha.RideSharing.dto.osrm;

public record OsrmRoute (
    double distance,
    OsrmGeometry geometry
){
}
