package com.hiepnt.moviebooking.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AvailableDateResponse {
    private String fullDate;  // Ngày đầy đủ (yyyy-MM-dd)
    private String date;      // Ngày với định dạng dd/MM
    private String dayOfWeek; // Thứ (hoặc "Hôm nay")
}
