package com.akshitha.RideSharing.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateRideRequest{
    @NotNull
    private Long driverId;
    @NotNull
    private Double sourceLat;
    @NotNull
    private Double sourceLng;
    @NotNull
    private Double destinationLat;
    @NotNull
    private Double destinationLng;
    @NotNull
    @Min(1)
    private Integer totalSeats;
    @NotNull
    @FutureOrPresent
    private LocalDateTime rideDateTime;
}
    

