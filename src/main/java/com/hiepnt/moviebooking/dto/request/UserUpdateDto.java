package com.hiepnt.moviebooking.dto.request;

import com.hiepnt.moviebooking.entity.enums.Gender;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateDto {
    @Pattern(regexp = "^\\d{10}$", message = "Wrong phone number format")
    String phoneNumber;

    String fullName;

    Gender gender;
}
