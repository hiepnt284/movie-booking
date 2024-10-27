package com.hiepnt.moviebooking.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShowtimeResponse {
    Integer id;
    LocalTime timeStart;

    LocalTime timeEnd;

    String movieTitle;

    int movieId;

    int roomId;

    Boolean isActive;
}
