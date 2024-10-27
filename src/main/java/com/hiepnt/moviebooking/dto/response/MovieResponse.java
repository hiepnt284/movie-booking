package com.hiepnt.moviebooking.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MovieResponse {
    Integer id;
    String title;
    String director;
    String cast;
    String ageRating;
    Integer duration;
    @JsonFormat(pattern = "dd-MM-yyyy")
    LocalDate releaseDate;
    String trailer;
    String description;
    String genre;
    String poster;
    Boolean isActive;
}
