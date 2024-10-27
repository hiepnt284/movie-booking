package com.hiepnt.moviebooking.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShowtimeByRoomType {
    String roomTypeName;
    int roomTypeId;
    List<ShowtimeByTimeResponse> showtimeByTimeResponseList;
}
