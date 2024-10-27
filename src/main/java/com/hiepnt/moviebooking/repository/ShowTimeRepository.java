package com.hiepnt.moviebooking.repository;

import com.hiepnt.moviebooking.entity.Room;
import com.hiepnt.moviebooking.entity.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface ShowTimeRepository extends JpaRepository<Showtime,Integer> {
    List<Showtime> findByRoomAndDate(Room room, LocalDate date);

    List<Showtime> findByMovie_IdAndRoom_RoomType_IdAndRoom_Theater_IdAndDateAndTimeStartAfter(
            int movieId, int roomTypeId, int theaterId, LocalDate date, LocalTime timeStart);

    @Query("SELECT DISTINCT s.date FROM Showtime s WHERE s.movie.id = :movieId AND s.isActive = true AND (s.date > :today OR (s.date = :today AND s.timeStart>=:timeNow))")
    List<LocalDate> findDistinctDatesByMovieIdAndFromToday(@Param("movieId") int movieId, @Param("today") LocalDate today, @Param("timeNow") LocalTime timeNow);

    List<Showtime> findByMovie_IdAndRoom_RoomType_IdAndRoom_Theater_IdAndDate(int movieId, Integer id, Integer id1, LocalDate date);
}
