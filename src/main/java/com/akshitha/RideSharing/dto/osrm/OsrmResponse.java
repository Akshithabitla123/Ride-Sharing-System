package com.akshitha.RideSharing.dto.osrm;

import java.util.List;

public record  OsrmResponse (
    List<OsrmRoute> routes
) {
    
}
