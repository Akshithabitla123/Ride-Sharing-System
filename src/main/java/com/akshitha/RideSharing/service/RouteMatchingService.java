package com.akshitha.RideSharing.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.akshitha.RideSharing.dto.NearestPoint;
import com.akshitha.RideSharing.entity.RoutePoint;
import com.akshitha.RideSharing.util.DistanceUtil;

@Service
public class RouteMatchingService {
    //find nearest points for the ride
     NearestPoint findNearestPoint(double rideLat,double rideLng,List<RoutePoint> routePoints){
        double minDistance=Double.MAX_VALUE;
        int nearestIndex=-1;
        for(RoutePoint point: routePoints){
            double distance=DistanceUtil.distanceMeters(rideLat, rideLng, point.getLatitude(), point.getLongitute());
            if(distance<minDistance){
                minDistance=distance;
                nearestIndex=point.getSequenceNo();
            }

        }
        return new NearestPoint(nearestIndex,minDistance);
    }

}
