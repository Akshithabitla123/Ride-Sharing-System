package com.akshitha.RideSharing.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.stereotype.Service;

import com.akshitha.RideSharing.dto.BookingResponse;
import com.akshitha.RideSharing.dto.Coordinate;
import com.akshitha.RideSharing.dto.CreateBookingRequest;
import com.akshitha.RideSharing.dto.NearestPoint;
import com.akshitha.RideSharing.entity.Booking;
import com.akshitha.RideSharing.entity.Ride;
import com.akshitha.RideSharing.entity.RoutePoint;
import com.akshitha.RideSharing.entity.SegmentInventory;
import com.akshitha.RideSharing.entity.User;
import com.akshitha.RideSharing.enums.BookingStatus;
import com.akshitha.RideSharing.enums.Role;
import com.akshitha.RideSharing.exceptions.BadRequestException;
import com.akshitha.RideSharing.exceptions.BookingException;
import com.akshitha.RideSharing.exceptions.ResourceNotFoundException;
import com.akshitha.RideSharing.repository.BookingRepo;
import com.akshitha.RideSharing.repository.RideRepo;
import com.akshitha.RideSharing.repository.RoutePointRepo;
import com.akshitha.RideSharing.repository.SegmentRepo;
import com.akshitha.RideSharing.repository.UserRepo;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepo bookingRepo;
    private final RideRepo rideRepo;
    private final RoutePointRepo routePointRepo;
    private final SegmentRepo segmentRepo;
    private final UserRepo userRepo;
    private final RouteMatchingService routeMatchingService;
    
    //create booking
    @Transactional
    public BookingResponse createBooking(CreateBookingRequest bookingRequest){
        //find ride
        Ride ride=rideRepo.findById(bookingRequest.rideId()).orElseThrow(()->new ResourceNotFoundException("Ride not found"));
        //find rider
        User rider=userRepo.findById(bookingRequest.riderId()).orElseThrow(()->new ResourceNotFoundException("Rider not found"));
        //validate rider
        if(rider.getRole()!=Role.RIDER){
            throw new BookingException("Only Rider is allowed to do bookings");
        }
        //get all the routepoints and find the nearest pickup and drop points
        List<RoutePoint> routePoints=routePointRepo.findByRideIdOrderBySequenceNo(ride.getId());
        NearestPoint pickup=routeMatchingService.findNearestPoint(bookingRequest.pickupLat(), bookingRequest.pickupLng(), routePoints);
        NearestPoint drop=routeMatchingService.findNearestPoint(bookingRequest.dropLat(), bookingRequest.dropLng(), routePoints);
        if(pickup.index()>=drop.index()){
            throw new BookingException("Pickup must come before drop location");
        }
        List<Booking> existingBookings=bookingRepo.findByRideIdAndRiderId(bookingRequest.rideId(),bookingRequest.riderId());
        for(Booking b:existingBookings){
            if(isOverlapping(pickup.index(),drop.index(),b.getPickupIndex(),b.getDropIndex())){
                throw new BadRequestException("You already booked an overlapping segment of this ride");
            }
        }
        final double MAX_RADIUS=500;
        if(pickup.distanceMeters()>MAX_RADIUS){
            throw new BadRequestException("Pickup point is too far from the route");
        }
        if(drop.distanceMeters()>MAX_RADIUS){
            throw new BadRequestException("Drop point is too far from the route");
        }
        List<SegmentInventory> segments=segmentRepo.lockSegments(bookingRequest.rideId(),pickup.index(),drop.index());
        for(SegmentInventory s:segments){
            if(s.getAvailableSeats()<bookingRequest.seatsBooked()){
                throw new BadRequestException("Seats available are less than required");
            }
        }
        for(SegmentInventory s:segments){
            s.setAvailableSeats(s.getAvailableSeats()-bookingRequest.seatsBooked());
        }
        segmentRepo.saveAll(segments);
        RoutePoint pickupPoint=routePoints.get(pickup.index());
        RoutePoint dropPoint=routePoints.get(drop.index());
        double distanceKm=dropPoint.getDistanceFromStartKm()-pickupPoint.getDistanceFromStartKm();
        distanceKm=BigDecimal.valueOf(distanceKm).setScale(2,RoundingMode.HALF_UP).doubleValue();
        //fare calculation
        double fare=distanceKm*ride.getFarePerKm();
        fare=BigDecimal.valueOf(fare).setScale(2,RoundingMode.HALF_UP).doubleValue();
        //create booking
        Booking booking=Booking.builder()
                            .ride(ride)
                            .rider(rider)
                            .pickupIndex(pickup.index())
                            .dropIndex(drop.index())
                            .seatsBooked(bookingRequest.seatsBooked())
                            .distanceKm(distanceKm)
                            .fare(fare)
                            .status(BookingStatus.CONFIRMED).build();
        
        Booking savedBooking=bookingRepo.save(booking);
        return new BookingResponse(
            savedBooking.getId(),
            ride.getId(),
            ride.getDriver().getName(),
            ride.getDriver().getPhone(),
            new Coordinate(pickupPoint.getLatitude(),pickupPoint.getLongitute()),
            new Coordinate(dropPoint.getLatitude(),dropPoint.getLongitute()),
            ride.getRideDateTime(),
            savedBooking.getSeatsBooked(),
            savedBooking.getDistanceKm(),
            savedBooking.getFare(),
            savedBooking.getStatus()
        );
    }

    private boolean isOverlapping(int p1,int d1, int p2, int d2){
        return p1<d2 && p2<d1;
    }
    public BookingResponse getBookingById(Long id){
        Booking booking=bookingRepo.findById(id).orElseThrow(()->new BookingException("Booking not found"));
        return mapToResponse(booking);
    }

    public List<BookingResponse> getBookingsByRider(Long riderId){
        User rider=userRepo.findById(riderId).orElseThrow(()->new ResourceNotFoundException("Rider not found"));
        if(rider.getRole()!=Role.RIDER){
            throw new BadRequestException("API not accessible");
        }
        return bookingRepo.findByRiderId(riderId).stream().map(this::mapToResponse).toList();
    }
    public List<BookingResponse> getBookingsOfDriver(Long driverId){
        User driver=userRepo.findById(driverId).orElseThrow(()->new BadRequestException("Driver not found"));
        if(driver.getRole()!=Role.DRIVER){
            throw new BadRequestException("API not accessible");
        }
        return bookingRepo.findByRideDriverId(driverId).stream().map(this::mapToResponse).toList();

    }

    private BookingResponse mapToResponse(Booking booking) {
        List<RoutePoint> routePoints=routePointRepo.findByRideIdOrderBySequenceNo(booking.getRide().getId());
        RoutePoint pickup =routePoints.get(booking.getPickupIndex());
        RoutePoint drop =routePoints.get(booking.getDropIndex());
        return new BookingResponse(
                booking.getId(),
                booking.getRide().getId(),
                booking.getRide().getDriver().getName(),
                booking.getRide().getDriver().getPhone(),
                new Coordinate(pickup.getLatitude(),pickup.getLongitute()),
                new Coordinate(drop.getLatitude(),drop.getLongitute()),
                booking.getRide().getRideDateTime(),
                booking.getSeatsBooked(),
                booking.getDistanceKm(),
                booking.getFare(),
                booking.getStatus()
        );
    }
    

}
