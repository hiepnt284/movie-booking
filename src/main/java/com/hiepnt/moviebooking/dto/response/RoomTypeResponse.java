package com.hiepnt.moviebooking.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomTypeResponse {
    Integer id;
    String name; //2D,3D,IMAX
    double extraPrice; //0,30,70
}
