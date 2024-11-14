package com.hiepnt.moviebooking.repository;

import com.hiepnt.moviebooking.entity.Booking;
import com.hiepnt.moviebooking.entity.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking,Integer> {
    List<Booking> findByStatus(BookingStatus bookingStatus);

    Booking findByBookingCode(String bookingCode);
}
