package com.hiepnt.moviebooking.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingItemResponse {
    Integer id;
    double totalPrice;
    LocalDateTime bookingDate;
    String movieTitle;
    String bookingCode;
    String theaterName;

    String roomName;
    String showSeatNumberList;
}
