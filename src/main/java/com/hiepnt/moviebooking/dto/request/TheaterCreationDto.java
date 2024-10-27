package com.hiepnt.moviebooking.dto.request;

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
public class TheaterCreationDto {
    @NotBlank(message = "Name is mandatory")
    String name;
    @NotBlank(message = "Address is mandatory")
    String address;
    String email;
    String phone;
    String description;
}
