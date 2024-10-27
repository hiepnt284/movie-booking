package com.hiepnt.moviebooking.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SelectAShowTimeResponse {
    String movieTitle;
    String moviePoster;
    String roomTypeName;
    String ageRating;
    String theaterName;
    String roomName;
    LocalDate date;
    LocalTime timeStart;

    List<ShowSeatResponse> showSeatResponseList;
}
