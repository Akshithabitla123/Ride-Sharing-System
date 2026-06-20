package com.akshitha.RideSharing.controller;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.akshitha.RideSharing.dto.CreateRideRequest;
import com.akshitha.RideSharing.dto.RideResponse;
import com.akshitha.RideSharing.dto.RideSearchResponse;
import com.akshitha.RideSharing.dto.SeatMapResponse;
import com.akshitha.RideSharing.enums.RideStatus;
import com.akshitha.RideSharing.service.RideService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/rides")
@RequiredArgsConstructor
@Tag(name="RIDE",description="Ride Management APIs")
public class RideController {
    private final RideService rideService;
    @PostMapping
    @Operation(summary="Create Ride")
    public RideResponse createRide(@Valid @RequestBody CreateRideRequest request){
        return rideService.createRide(request);
    }
    @Operation(summary="Get ride by id")
    @GetMapping("/{id}")
    public RideResponse getRide(@PathVariable Long id){
        return rideService.getRideById(id);
    }
    @Operation(summary="Get all rides")
    @GetMapping
    public List<RideResponse> getAllRides(){
        return rideService.getAllRides();
    }
    @GetMapping("/{id}/seat-map")
    @Operation(summary="Get seat map")
    public List<SeatMapResponse> getSeatMap(@PathVariable Long id){
        return rideService.getSeatMap(id);
    }
    //search for matching rides
    @Operation(summary="Search for matching rides")
    @GetMapping("/search")
    public List<RideSearchResponse> searchRides(@RequestParam Double sourceLat,@RequestParam Double sourceLng,@RequestParam Double destinationLat,@RequestParam Double destinationLng){
        return rideService.searchRides(sourceLat, sourceLng, destinationLat, destinationLng);
    }
    //get status of ride
    @Operation(summary="Check whether route points are calculated or not")
    @GetMapping("/status/{rideId}")
    public RideStatus checkStatus(@PathVariable Long rideId){
        return rideService.checkRideStatus(rideId);
    }
}
