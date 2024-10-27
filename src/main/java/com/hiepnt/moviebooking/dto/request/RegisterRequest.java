package com.hiepnt.moviebooking.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hiepnt.moviebooking.entity.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisterRequest {
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is mandatory")
    String email;

    @Size(min = 6, message = "The password must have a minimum length of 6 characters")
    String password;

    @Pattern(regexp = "^\\d{10}$", message = "Invalid phone number format")
    @NotBlank(message = "phone number is mandatory")
    String phoneNumber;

    @NotBlank(message = "full name is mandatory")
    String fullName;

    Gender gender;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @JsonFormat(pattern = "dd/MM/yyyy")
    LocalDate dob;
}
