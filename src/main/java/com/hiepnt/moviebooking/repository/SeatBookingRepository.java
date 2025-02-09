package com.hiepnt.moviebooking.repository;

import com.hiepnt.moviebooking.entity.SeatBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeatBookingRepository extends JpaRepository<SeatBooking, Integer> {
}
