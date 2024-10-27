package com.hiepnt.moviebooking.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hiepnt.moviebooking.entity.enums.Gender;
import com.hiepnt.moviebooking.entity.enums.Role;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    int id;
    String email;
    String phoneNumber;
    String fullName;
    Gender gender;
    @JsonFormat(pattern = "dd-MM-yyyy")
    LocalDate dob;
    String avt;
    Role role;
}
