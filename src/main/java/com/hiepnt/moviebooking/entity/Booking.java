package com.hiepnt.moviebooking.entity;

import com.hiepnt.moviebooking.entity.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    double totalPrice;
    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    LocalDateTime bookingDate;

    @ManyToOne
    @JoinColumn(name = "showtime_id")
    Showtime showtime;

    BookingStatus status;

    String showSeatNumberList;
}
