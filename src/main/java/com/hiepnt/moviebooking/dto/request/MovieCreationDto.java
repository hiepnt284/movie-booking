package com.hiepnt.moviebooking.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MovieCreationDto {
    @NotBlank(message = "Title is mandatory")
    String title;
    String director;
    String cast;
    String ageRating;
    @Min(value = 1, message = "Duration must be at least 1 minute")
    @NotNull(message = "Duration is mandatory")
    Integer duration;
    @NotNull(message = "ReleaseDate is mandatory")
    @JsonFormat(pattern = "dd-MM-yyyy")
    LocalDate releaseDate;
    @NotBlank(message = "Trailer is mandatory")
    String trailer;
    @Size(max = 1000, message = "Description can be up to 1000 characters")
    @NotBlank(message = "Description is mandatory")
    String description;
    String genre;
    @NotNull(message = "isActive is mandatory")
    Boolean isActive;

}
