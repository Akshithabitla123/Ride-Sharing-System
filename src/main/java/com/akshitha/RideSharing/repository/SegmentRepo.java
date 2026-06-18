package com.akshitha.RideSharing.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import com.akshitha.RideSharing.entity.SegmentInventory;

import jakarta.persistence.LockModeType;

public interface SegmentRepo extends JpaRepository<SegmentInventory,Long>{
    List<SegmentInventory> findByRideIdOrderByFromIndex(Long rideId);
    List<SegmentInventory> findByRideId(Long rideId);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT s FROM SegmentInventory s WHERE s.ride.id=:rideId
            AND s.fromIndex>=:from AND s.toIndex<=:to
            """)
    List<SegmentInventory> lockSegments(Long rideId,int from, int to);
}
