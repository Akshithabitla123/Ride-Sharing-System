package com.akshitha.RideSharing.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.akshitha.RideSharing.dto.Coordinate;
import com.akshitha.RideSharing.dto.CreateRideRequest;
import com.akshitha.RideSharing.dto.NearestPoint;
import com.akshitha.RideSharing.dto.RideResponse;
import com.akshitha.RideSharing.dto.RideSearchResponse;
import com.akshitha.RideSharing.dto.SeatMapResponse;
import com.akshitha.RideSharing.entity.Ride;
import com.akshitha.RideSharing.entity.RoutePoint;
import com.akshitha.RideSharing.entity.User;
import com.akshitha.RideSharing.enums.RideStatus;
import com.akshitha.RideSharing.enums.Role;
import com.akshitha.RideSharing.exceptions.BadRequestException;
import com.akshitha.RideSharing.exceptions.ResourceNotFoundException;
import com.akshitha.RideSharing.repository.RideRepo;
import com.akshitha.RideSharing.repository.RoutePointRepo;
import com.akshitha.RideSharing.repository.SegmentRepo;
import com.akshitha.RideSharing.repository.UserRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RideService {
    private final UserRepo userRepo;
    private final RideRepo rideRepo;
    private final RoutePointRepo routePointRepo;
    private final SegmentRepo segmentRepo;
    private final RouteMatchingService routeMatchingService;
    private final RideRouteProcessor routeProcessor;

    public RideResponse createRide(CreateRideRequest request){
        long start=System.currentTimeMillis();
        User driver=userRepo.findById(request.getDriverId()).orElseThrow(()->new ResourceNotFoundException("Driver not found with given id"));
        if(driver.getRole()!=Role.DRIVER){
            throw new BadRequestException("Only Driver is allowed to add the rides");
        }
        Ride ride=Ride.builder()
                        .driver(driver)
                        .sourceLat(request.getSourceLat())
                        .sourceLng(request.getSourceLng())
                        .destinationLat(request.getDestinationLat())
                        .destinationLng(request.getDestinationLng())
                        .totalSeats(request.getTotalSeats())
                        .routeDistance(0.0)
                        .rideDateTime(request.getRideDateTime())
                        .farePerKm(request.getFarePerKm())
                        .status(RideStatus.PROCESSING)
                        .build();
        Ride savedRide=rideRepo.save(ride);
        routeProcessor.generateRouteData(savedRide.getId());
        long end=System.currentTimeMillis();
        System.out.println("Create Ride API response time: "+(end-start)+" ms");
        
        return mapToResponse(savedRide,0);
    }

    private RideResponse mapToResponse(Ride ride,int totalPoints){
        return new RideResponse(ride.getId(),ride.getDriver().getId(),ride.getDriver().getName(),ride.getRouteDistance(),totalPoints,ride.getRideDateTime(),ride.getStatus());
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

    //get seat map
    public List<SeatMapResponse> getSeatMap(Long rideId){
        rideRepo.findById(rideId).orElseThrow(()->new ResourceNotFoundException("Ride not found"));
        return segmentRepo.findByRideIdOrderByFromIndex(rideId)
                          .stream()
                          .map(segment->new SeatMapResponse(
                                segment.getFromIndex(),
                                segment.getToIndex(),
                                segment.getAvailableSeats()
        )).toList();
    }

    
    //search rides
    private static final double SEARCH_RADIUS=1500;
    public List<RideSearchResponse> searchRides(double sourceLat,double sourceLng,double destinationLat,double destinationLng){
        List<Ride> rides=rideRepo.findByStatus(RideStatus.ACTIVE);
        List<RideSearchResponse> matches=new ArrayList<>();
        for(Ride ride:rides){
            List<RoutePoint> routePoints=routePointRepo.findByRideIdOrderBySequenceNo(ride.getId());
            if(routePoints.isEmpty()) continue;
            NearestPoint pickUp=routeMatchingService.findNearestPoint(sourceLat, sourceLng, routePoints);
            NearestPoint drop=routeMatchingService.findNearestPoint(destinationLat, destinationLng, routePoints);
            boolean validDirection=pickUp.index()<drop.index();
            boolean pickUpWithinRadius=pickUp.distanceMeters()<=SEARCH_RADIUS;
            boolean dropWithinRadius=drop.distanceMeters()<=SEARCH_RADIUS;
            System.out.println(
                    "Ride=" + ride.getId()
                );

                System.out.println(
                    "Pickup Index=" + pickUp.index()
                );

                System.out.println(
                    "Drop Index=" + drop.index()
                );

                System.out.println(
                    "Pickup Distance=" + pickUp.distanceMeters()
                );

                System.out.println(
                    "Drop Distance=" + drop.distanceMeters()
                );
            if(validDirection && pickUpWithinRadius && dropWithinRadius){
                RoutePoint pickUpPoint=routePoints.get(pickUp.index());
                RoutePoint dropPoint=routePoints.get(drop.index());
                matches.add(new RideSearchResponse(
                    ride.getId(),
                    ride.getDriver().getName(),
                    new Coordinate(pickUpPoint.getLatitude(),pickUpPoint.getLongitute()),
                    new Coordinate(dropPoint.getLatitude(),dropPoint.getLongitute()),
                    ride.getRideDateTime(),
                    pickUp.distanceMeters(),
                    drop.distanceMeters()
                ));
                
            }
        }
        
        return matches;
    }

    //check status of ride
    public RideStatus checkRideStatus(Long rideId){
        Ride ride=rideRepo.findById(rideId).orElseThrow(()->new ResourceNotFoundException("Ride not found"));
        return ride.getStatus();
    }

}
