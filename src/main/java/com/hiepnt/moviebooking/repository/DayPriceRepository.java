package com.hiepnt.moviebooking.repository;

import com.hiepnt.moviebooking.entity.DayPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.Optional;

@Repository
public interface DayPriceRepository extends JpaRepository<DayPrice,Integer> {
    Optional<DayPrice> findByDayOfWeek(DayOfWeek dayOfWeek);
}
