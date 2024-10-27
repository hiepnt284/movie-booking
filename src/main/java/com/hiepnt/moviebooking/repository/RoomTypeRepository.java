package com.hiepnt.moviebooking.repository;

import com.hiepnt.moviebooking.entity.RoomType;
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
public interface RoomTypeRepository extends JpaRepository<RoomType,Integer> {


    List<RoomType> findDistinctByRooms_Theater_IdAndRooms_ShowTimes_DateAndRooms_ShowTimes_TimeStartAfter(int theaterId, LocalDate date, LocalTime timeStart);
    boolean existsByName(String name);

    @Query("SELECT rt FROM RoomType rt WHERE LOWER(rt.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<RoomType> findByNameContainingIgnoreCase(@Param("keyword") String keyword, Pageable pageable);

    List<RoomType> findDistinctByRooms_Theater_IdAndRooms_ShowTimes_Date(Integer id, LocalDate date);
}
