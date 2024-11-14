package com.hiepnt.moviebooking.repository;

import com.hiepnt.moviebooking.entity.FoodBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodBookingRepository extends JpaRepository<FoodBooking, Integer> {
}
