package com.hiepnt.moviebooking.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RecoverPasswordDto {

    String otp;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is mandatory")
    String email;

    @Size(min = 6, message = "The password must have a minimum length of 6 characters")
    String newPassword;

    String confirmPassword;
}
