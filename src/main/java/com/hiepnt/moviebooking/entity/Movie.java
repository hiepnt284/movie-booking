package com.hiepnt.moviebooking.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    @Column(unique = true)
    String title;
    String director;
    String cast;
    String ageRating;
    Integer duration;
    LocalDate releaseDate;
    String trailer;
    @Column(length = 1000)
    String description;
    String genre;
    String poster;
    Boolean isActive;

    @OneToMany(mappedBy = "movie", fetch = FetchType.LAZY)
    List<Showtime> showTimes;
}
