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
public class SeatType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    String name; //STANDARD,VIP,COUPLE
    double extraPrice; //0,10,50
    @OneToMany(mappedBy = "seatType")
    List<RoomSeat> roomSeats;
}
