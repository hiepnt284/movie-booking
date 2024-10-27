package com.hiepnt.moviebooking.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TheaterResponse {
    Integer id;
    String name;
    String address;
    String email;
    String phone;
    String description;
    String img;
}
