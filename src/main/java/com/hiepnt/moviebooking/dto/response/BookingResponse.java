package com.hiepnt.moviebooking.dto.response;

import com.hiepnt.moviebooking.entity.FoodBooking;
import com.hiepnt.moviebooking.entity.SeatBooking;
import com.hiepnt.moviebooking.entity.Showtime;
import com.hiepnt.moviebooking.entity.User;
import com.hiepnt.moviebooking.entity.enums.BookingStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingResponse {
    Integer id;
    double totalPrice;
    String userName;
    String email;
    LocalDateTime bookingDate;

    String movieTitle;

    LocalDate date;

    LocalTime timeStart;
    String theaterName;

    String roomName;
    String showSeatNumberList;

    List<FoodBookingResponse> foodBookingList;

    String bookingCode;

    String qrCode;

    Boolean isUsed;
}
