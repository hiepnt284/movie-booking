package com.hiepnt.moviebooking.repository;

import com.hiepnt.moviebooking.entity.RoomType;
import com.hiepnt.moviebooking.entity.SeatType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SeatTypeRepository extends JpaRepository<SeatType,Integer> {
    boolean existsByName(String name);

    Optional<SeatType> findByName(String name);

    @Query("SELECT st FROM SeatType st WHERE LOWER(st.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<SeatType> findByNameContainingIgnoreCase(@Param("keyword") String keyword, Pageable pageable);
}
