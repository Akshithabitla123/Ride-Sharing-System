package com.akshitha.RideSharing.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.akshitha.RideSharing.dto.RouteDetails;
import com.akshitha.RideSharing.entity.Ride;
import com.akshitha.RideSharing.entity.RoutePoint;
import com.akshitha.RideSharing.entity.SegmentInventory;
import com.akshitha.RideSharing.enums.RideStatus;
import com.akshitha.RideSharing.repository.RideRepo;
import com.akshitha.RideSharing.repository.RoutePointRepo;
import com.akshitha.RideSharing.repository.SegmentRepo;
import com.akshitha.RideSharing.util.DistanceUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RideRouteProcessor {
    private final RideRepo rideRepo;
    private final RouteService routeService;
    private final RoutePointRepo routePointRepo;
    private final SegmentRepo segmentRepo;

    @Async
    @Transactional
    public void generateRouteData(Long rideId){
        long start=System.currentTimeMillis();
        try{
            System.out.println("Started processing ride "+rideId);
            Ride ride=rideRepo.findById(rideId).orElseThrow();
            long t1=System.currentTimeMillis();
            RouteDetails routeDetails=routeService.getCompleteRoute(ride.getSourceLat(),ride.getSourceLng(),ride.getDestinationLat(),ride.getDestinationLng());
            long t2=System.currentTimeMillis();
            List<RoutePoint> routePoints=buildRoutePoints(ride,routeDetails.coordinates());
            long t3=System.currentTimeMillis();
            routePointRepo.saveAll(routePoints);
            long t4=System.currentTimeMillis();
            List<SegmentInventory> inventories=buildInventories(ride,routePoints,ride.getTotalSeats());
            long t5=System.currentTimeMillis();
            segmentRepo.saveAll(inventories);
            long t6=System.currentTimeMillis();
            ride.setStatus(RideStatus.ACTIVE);
            ride.setRouteDistance(routeDetails.distanceKm());
            rideRepo.save(ride);
            long t7=System.currentTimeMillis();
            System.out.println("Async Ride Processing");
            System.out.println("OSRM Call        : " + (t2 - t1) + " ms");
            System.out.println("Build RoutePts   : " + (t3 - t2) + " ms");
            System.out.println("Save RoutePts    : " + (t4 - t3) + " ms");
            System.out.println("Build Inventory  : " + (t5 - t4) + " ms");
            System.out.println("Save Inventory   : " + (t6 - t5) + " ms");
            System.out.println("Update Ride      : " + (t7 - t6) + " ms");
            System.out.println("TOTAL            : " + (t7 - start) + " ms");
            System.out.println("Total route points: "+routePoints.size());
            log.info("Ride {} activated",rideId);
        }catch(Exception e){
            log.error("Failed processing ride: {}",rideId,e);
            rideRepo.findById(rideId).ifPresent(r->{
                r.setStatus(RideStatus.FAILED);
                rideRepo.save(r);
            });
        }
    }

    public List<RoutePoint> buildRoutePoints(Ride ride,List<List<Double>> coordinates){
         List<RoutePoint> routePoints=new ArrayList<>();
        double cummulativeDistance=0.0;
        for(int i=0;i<coordinates.size();i++){
            List<Double> points=coordinates.get(i);
            if(i>0){
                List<Double> prev=coordinates.get(i-1);
                cummulativeDistance+=DistanceUtil.distanceMeters(prev.get(1),prev.get(0) ,points.get(1) ,points.get(0) )/1000.0;
            }
            RoutePoint routePoint=RoutePoint.builder()
                        .ride(ride)
                        .sequenceNo(i)
                        .longitute(points.get(0))
                        .latitude(points.get(1))
                        .distanceFromStartKm(cummulativeDistance)
                        .build();
            routePoints.add(routePoint);
        }
        return routePoints;
    }

    public List<SegmentInventory> buildInventories(Ride ride,List<RoutePoint> routePoints,int seats){
        List<SegmentInventory> inventories=new ArrayList<>();
        for(int i=0;i<routePoints.size()-1;i++){
            SegmentInventory inventory=SegmentInventory.builder()
                                                .ride(ride)
                                                .fromIndex(i)
                                                .toIndex(i+1)
                                                .availableSeats(seats)
                                                .build();
            inventories.add(inventory);
        }
        return inventories;
    }
}
