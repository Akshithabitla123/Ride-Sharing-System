package com.akshitha.RideSharing.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.akshitha.RideSharing.dto.RouteResponse;
import com.akshitha.RideSharing.service.RouteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/routes")
@RequiredArgsConstructor
@Tag(name="ROUTE",description="Route Management APIs")
public class RouteController {
    private final RouteService routeService;

    @Operation(summary="Test OSRM route")
    @GetMapping("/test")
    public RouteResponse testRoute(@RequestParam double srcLat,@RequestParam double srcLng,@RequestParam double destLat,@RequestParam double destLng){
        return routeService.getRouteDetails(srcLat, srcLng, destLat, destLng);
    }
}
