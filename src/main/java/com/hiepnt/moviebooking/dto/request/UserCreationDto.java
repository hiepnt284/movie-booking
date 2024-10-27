package com.hiepnt.moviebooking.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hiepnt.moviebooking.entity.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationDto {
    @Email(message = "Wrong email format")
    String email;

    @Size(min = 6, message = "The password must have a minimum length of 6 characters")
    String password;

    @Pattern(regexp = "^\\d{10}$", message = "Wrong phone number format")
    String phoneNumber;

    String fullName;

    Gender gender;
    @JsonFormat(pattern = "dd-MM-yyyy")
    LocalDate dob;
}
