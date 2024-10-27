package com.hiepnt.moviebooking.dto.response;

import com.hiepnt.moviebooking.entity.SeatType;
import com.hiepnt.moviebooking.entity.enums.ShowSeatStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShowSeatResponse {
    Integer id;
    ShowSeatStatus showSeatStatus;
    double price;
    String seatTypeName;
    String seatRow;
    int number;
}
