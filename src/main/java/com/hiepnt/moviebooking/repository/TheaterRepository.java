package com.hiepnt.moviebooking.repository;

import com.hiepnt.moviebooking.entity.Movie;
import com.hiepnt.moviebooking.entity.Theater;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface TheaterRepository extends JpaRepository<Theater,Integer> {

    boolean existsByName(String name);

    @Query("SELECT m FROM Theater m WHERE LOWER(m.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Theater> findByTitleOrDescriptionContainingIgnoreCase(@Param("keyword") String keyword, Pageable pageable);


    List<Theater> findDistinctByRooms_ShowTimes_Movie_IdAndRooms_ShowTimes_IsActiveTrueAndRooms_ShowTimes_DateAndRooms_ShowTimes_TimeStartAfter(int movieId, LocalDate date, LocalTime timeStart);

    List<Theater> findDistinctByRooms_ShowTimes_Movie_IdAndRooms_ShowTimes_IsActiveTrueAndRooms_ShowTimes_Date(int movieId, LocalDate date);
}
