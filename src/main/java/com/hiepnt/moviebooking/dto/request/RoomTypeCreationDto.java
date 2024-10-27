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
public class RoomTypeCreationDto {
    @NotBlank(message = "Name is mandatory")
    String name; //2D,3D,IMAX
    @Min(value = 0, message = "Min is 0")
    double extraPrice; //0,30,70
}
