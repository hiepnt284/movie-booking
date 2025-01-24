package com.hiepnt.moviebooking.repository;

import com.hiepnt.moviebooking.entity.Room;
import com.hiepnt.moviebooking.entity.RoomSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomSeatRepository extends JpaRepository<RoomSeat,Integer> {
    void deleteAllByRoom(Room room);
}
