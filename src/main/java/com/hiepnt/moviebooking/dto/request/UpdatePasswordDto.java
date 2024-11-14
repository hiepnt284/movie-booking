package com.hiepnt.moviebooking.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdatePasswordDto {
    String oldPassword;

    @Size(min = 6, message = "The password must have a minimum length of 6 characters")
    String newPassword;

    String confirmPassword;
}
