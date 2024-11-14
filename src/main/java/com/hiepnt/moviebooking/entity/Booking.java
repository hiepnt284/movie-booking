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

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    List<FoodBooking> foodBookingList;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    List<SeatBooking> seatBookingList;

    String showSeatNumberList;

    String bookingCode;

    String qrCode;

    Boolean isUsed;
}
