package com.akshitha.RideSharing.service;

import org.springframework.stereotype.Service;

import com.akshitha.RideSharing.client.OsrmClient;
import com.akshitha.RideSharing.dto.RouteDetails;
import com.akshitha.RideSharing.dto.RouteResponse;
import com.akshitha.RideSharing.dto.osrm.OsrmResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RouteService {
    private final OsrmClient osrmClient;
    public RouteResponse getRouteDetails(double srcLat,double srcLng, double destLat, double destLng){
        OsrmResponse response=osrmClient.getRoute(srcLat, srcLng, destLat, destLng);
        if(response==null || response.routes()==null || response.routes().isEmpty()){
            throw new RuntimeException("Unable to calculate route");
        }
        var route=response.routes().get(0);
        return new RouteResponse(
            route.distance()/1000.0,
            route.geometry().coordinates().size()
        );
    }
    public RouteDetails getCompleteRoute(double srcLat,double srcLng, double destLat, double destLng){
        OsrmResponse response=osrmClient.getRoute(srcLat, srcLng, destLat, destLng);
        if(response==null || response.routes()==null || response.routes().isEmpty()){
            throw new RuntimeException("Unable to calculate route");
        }
        var route=response.routes().get(0);
        return new RouteDetails(
            route.distance()/1000.0,
            route.geometry().coordinates()
        );
    }
}
