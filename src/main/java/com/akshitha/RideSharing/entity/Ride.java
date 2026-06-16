package com.akshitha.RideSharing.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="rides")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ride {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name="driver_id")
    private User driver;
    @Column(nullable=false)
    private Double sourceLat;
    @Column(nullable=false)
    private Double sourceLng;
    @Column(nullable=false)
    private Double destinationLat;
    @Column(nullable=false)
    private Double destinationLng;
    @Column(nullable=false)
    private Double routeDistance;
    @Column(nullable=false)
    private LocalDateTime rideDateTime;
}
