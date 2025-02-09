package com.hiepnt.moviebooking.dto.request;

import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomUpdateDto {
    String name;
    int roomTypeId;
    @Min(value = 5, message = "min value row is 5")
    int row;
    @Min(value = 5, message = "min value col is 5")
    int col;
}
