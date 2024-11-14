package com.hiepnt.moviebooking.entity;

import com.hiepnt.moviebooking.entity.enums.ShowSeatStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShowSeat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    @Builder.Default
    ShowSeatStatus showSeatStatus = ShowSeatStatus.AVAILABLE;

    double price;

    String seatTypeName;

    String seatRow;

    int number;

    @ManyToOne
    @JoinColumn(name = "show_time_id")
    Showtime showtime;

//    @ManyToOne
//    @JoinColumn(name = "room_seat_id")
//    private RoomSeat roomSeat;



}
