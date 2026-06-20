package com.akshitha.RideSharing.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.akshitha.RideSharing.dto.BookingResponse;
import com.akshitha.RideSharing.dto.CreateBookingRequest;
import com.akshitha.RideSharing.service.BookingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@CrossOrigin(origins="*")
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@Tag(name="Booking",description="Booking Management APIs")
public class BookingController {
    private final BookingService bookingService;
    //create booking
    @PostMapping
    @Operation(summary="Create Booking")
    public BookingResponse createBooking(@Valid @RequestBody CreateBookingRequest request){
        return bookingService.createBooking(request);
    }    
    @GetMapping("/{id}")
    @Operation(summary="Get Booking details by Id")
    public BookingResponse getBookingById(@PathVariable Long id){
        return bookingService.getBookingById(id);
    }
    @GetMapping("/rider/{id}")
    @Operation(summary="Only accessible for riders")
    public List<BookingResponse> getBookingsByRider(@PathVariable Long id){
        return bookingService.getBookingsByRider(id);
    }
    @GetMapping("/driver/{id}")
    @Operation(summary="Only accessible for drivers")
    public List<BookingResponse> getBookingsOfDriver(@PathVariable Long id){
        return bookingService.getBookingsOfDriver(id);
    }

    @PatchMapping("/cancel/{bookingId}/{riderId}")
    @Operation(summary="Cancel the booking")
    public String cancelBooking(@PathVariable Long bookingId,@PathVariable Long riderId){
        return bookingService.cancelBooking(bookingId,riderId);
    }
    //ride completion flow
    @PutMapping("/driver-complete/{bookingId}/{driverId}")
    @Operation(summary="Complete the ride from driver side")
    public String driverComplete(@PathVariable Long bookingId,@PathVariable Long driverId){
        return bookingService.driverComplete(bookingId, driverId);
    }
    @PutMapping("/rider-complete/{bookingId}/{riderId}")
    @Operation(summary="Complete the ride from rider side")
    public String riderComplete(@PathVariable Long bookingId,@PathVariable Long riderId){
        return bookingService.riderComplete(bookingId, riderId);
    }

}
