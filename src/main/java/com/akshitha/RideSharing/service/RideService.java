package com.akshitha.RideSharing.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.akshitha.RideSharing.dto.Coordinate;
import com.akshitha.RideSharing.dto.CreateRideRequest;
import com.akshitha.RideSharing.dto.NearestPoint;
import com.akshitha.RideSharing.dto.RideResponse;
import com.akshitha.RideSharing.dto.RideSearchResponse;
import com.akshitha.RideSharing.dto.RouteDetails;
import com.akshitha.RideSharing.dto.SeatMapResponse;
import com.akshitha.RideSharing.entity.Ride;
import com.akshitha.RideSharing.entity.RoutePoint;
import com.akshitha.RideSharing.entity.SegmentInventory;
import com.akshitha.RideSharing.entity.User;
import com.akshitha.RideSharing.enums.Role;
import com.akshitha.RideSharing.exceptions.BadRequestException;
import com.akshitha.RideSharing.exceptions.ResourceNotFoundException;
import com.akshitha.RideSharing.repository.RideRepo;
import com.akshitha.RideSharing.repository.RoutePointRepo;
import com.akshitha.RideSharing.repository.SegmentRepo;
import com.akshitha.RideSharing.repository.UserRepo;
import com.akshitha.RideSharing.util.DistanceUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RideService {
    private final UserRepo userRepo;
    private final RideRepo rideRepo;
    private final RoutePointRepo routePointRepo;
    private final RouteService routeService;
    private final SegmentRepo segmentRepo;
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
                        .totalSeats(request.getTotalSeats())
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
        List<SegmentInventory> inventories=new ArrayList<>();
        for(int i=0;i<routePoints.size();i++){
            SegmentInventory inventory=SegmentInventory.builder()
                                                .ride(savedRide)
                                                .fromIndex(i)
                                                .toIndex(i+1)
                                                .availableSeats(savedRide.getTotalSeats())
                                                .build();
            inventories.add(inventory);
        }
        segmentRepo.saveAll(inventories);
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

    //find nearest points for the ride
    private NearestPoint findNearestPoint(double rideLat,double rideLng,List<RoutePoint> routePoints){
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

    //search rides
    private static final double SEARCH_RADIUS=500;
    public List<RideSearchResponse> searchRides(double sourceLat,double sourceLng,double destinationLat,double destinationLng){
        List<Ride> rides=rideRepo.findAll();
        List<RideSearchResponse> matches=new ArrayList<>();
        for(Ride ride:rides){
            List<RoutePoint> routePoints=routePointRepo.findByRideIdOrderBySequenceNo(ride.getId());
            if(routePoints.isEmpty()) continue;
            NearestPoint pickUp=findNearestPoint(sourceLat, sourceLng, routePoints);
            NearestPoint drop=findNearestPoint(destinationLat, destinationLng, routePoints);
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

}
