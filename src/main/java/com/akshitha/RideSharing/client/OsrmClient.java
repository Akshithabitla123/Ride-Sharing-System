package com.akshitha.RideSharing.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.akshitha.RideSharing.dto.osrm.OsrmResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OsrmClient {
    private final RestClient restClient;
    @Value("${osrm.base-url}")
    private String baseUrl;
    public OsrmResponse getRoute(double srcLat,double srcLng,double destLat,double destLng){
        String url=baseUrl+"/route/v1/driving/"+srcLng+","+srcLat
                +";"+destLng+","+destLat+"?overview=full"+"&geometries=geojson";
        return restClient.get().uri(url).retrieve().body(OsrmResponse.class);
    }
}
