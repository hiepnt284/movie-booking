package com.hiepnt.moviebooking.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MovieStatsDTO {
     String movieTitle;
     double revenue;  // Doanh thu (doanh thu từ vé - tổng tiền thức ăn)
     int showtimeCount;  // Số suất chiếu
     int ticketSold;  // Số vé đã bán
}
