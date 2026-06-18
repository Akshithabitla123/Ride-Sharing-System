package com.akshitha.RideSharing.entity;

import com.akshitha.RideSharing.enums.BookingStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name="bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name="ride_id")
    private Ride ride;
    @ManyToOne
    @JoinColumn(name="rider_id")
    private User rider;
    private Integer pickupIndex;
    private Integer dropIndex;
    private Integer seatsBooked;
    private Double distanceKm;
    private Double fare;
    @Enumerated(EnumType.STRING)
    private BookingStatus status;
}
