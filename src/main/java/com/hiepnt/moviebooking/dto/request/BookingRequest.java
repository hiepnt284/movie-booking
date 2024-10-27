package com.hiepnt.moviebooking.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingRequest {
    int userId;
    double totalPrice;
    LocalDateTime bookingDate;
    int showtimeId;
    List<Integer> listShowSeatId;
    String listShowSeatNumber;
}
