package com.hiepnt.moviebooking.repository;

import com.hiepnt.moviebooking.entity.Room;
import com.hiepnt.moviebooking.entity.Theater;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room,Integer> {
    boolean existsByName(String name);

    List<Room> findByTheater(Theater theater);
}
