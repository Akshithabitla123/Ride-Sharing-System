package com.akshitha.RideSharing.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.akshitha.RideSharing.dto.CreateRideRequest;
import com.akshitha.RideSharing.dto.RideResponse;
import com.akshitha.RideSharing.dto.RouteDetails;
import com.akshitha.RideSharing.entity.Ride;
import com.akshitha.RideSharing.entity.RoutePoint;
import com.akshitha.RideSharing.entity.User;
import com.akshitha.RideSharing.enums.Role;
import com.akshitha.RideSharing.exceptions.BadRequestException;
import com.akshitha.RideSharing.exceptions.ResourceNotFoundException;
import com.akshitha.RideSharing.repository.RideRepo;
import com.akshitha.RideSharing.repository.RoutePointRepo;
import com.akshitha.RideSharing.repository.UserRepo;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RideService {
    private final UserRepo userRepo;
    private final RideRepo rideRepo;
    private final RoutePointRepo routePointRepo;
    private final RouteService routeService;
    @Transactional
    public RideResponse createRide(CreateRideRequest request){
        User driver=userRepo.findById(request.getDriverId()).orElseThrow(()->new ResourceNotFoundException("Driver not found with given id"));
        if(driver.getRole()!=Role.DRIVER){
            throw new BadRequestException("Only Driver is allowed to add the rides");
        }
        RouteDetails routeDetails=routeService.getCompleteRoute(request.getSourceLat(),request.getSourceLng(),request.getDestinationLat(),request.getDestinationLng());
        Ride ride=Ride.builder()
                        .driver(driver)
                        .sourceLat(request.getSourceLat())
                        .sourceLng(request.getSourceLng())
                        .destinationLat(request.getDestinationLat())
                        .destinationLng(request.getDestinationLng())
                        .routeDistance(routeDetails.distanceKm())
                        .rideDateTime(request.getRideDateTime())
                        .build();
        Ride savedRide=rideRepo.save(ride);

        List<RoutePoint> routePoints=new ArrayList<>();
        List<List<Double>> coordinates=routeDetails.coordinates();
        for(int i=0;i<coordinates.size();i++){
            List<Double> points=coordinates.get(i);
            RoutePoint routePoint=RoutePoint.builder()
                        .ride(savedRide)
                        .sequenceNo(i)
                        .longitute(points.get(0))
                        .latitude(points.get(1))
                        .build();
            routePoints.add(routePoint);
        }
        routePointRepo.saveAll(routePoints);
        return mapToResponse(savedRide, routePoints.size());
    }

    private RideResponse mapToResponse(Ride ride,int totalPoints){
        return new RideResponse(ride.getId(),ride.getDriver().getId(),ride.getDriver().getName(),ride.getRouteDistance(),totalPoints,ride.getRideDateTime());
    }

    public RideResponse getRideById(Long id){
        Ride ride= rideRepo.findById(id).orElseThrow(()->new ResourceNotFoundException("Cannot get the ride"));
        int totalPoints=routePointRepo.countByRideId(id);
        return mapToResponse(ride, totalPoints);

    }
    public List<RideResponse> getAllRides(){
        return rideRepo.findAll().stream()
                .map(ride->{
                    int totalPoints=routePointRepo.countByRideId(ride.getId());
                    return mapToResponse(ride, totalPoints);
                })
                .toList();
    }

}
