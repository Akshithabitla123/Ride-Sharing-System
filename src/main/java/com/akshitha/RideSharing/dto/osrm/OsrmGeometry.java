package com.akshitha.RideSharing.dto.osrm;

import java.util.List;

public record OsrmGeometry (
    List<List<Double>> coordinates
){
    
}
