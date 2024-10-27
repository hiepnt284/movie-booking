package com.hiepnt.moviebooking.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomCreationDto {
    @NotBlank(message = "Name is mandatory")
    String name;
    int roomTypeId;
    int theaterId;
    @Min(value = 5, message = "min value row is 5")
    int row;
    @Min(value = 5, message = "min value col is 5")
    int col;
}
