package com.hiepnt.moviebooking.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomSeat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    String seatRow;
    int number;

    @ManyToOne
    @JoinColumn(name = "room_id")
    Room room;

    @OneToMany(mappedBy = "roomSeat", cascade = CascadeType.ALL, orphanRemoval = true)
    List<ShowSeat> showSeats;

    @ManyToOne
    @JoinColumn(name = "seat_type")
    SeatType seatType;

}
