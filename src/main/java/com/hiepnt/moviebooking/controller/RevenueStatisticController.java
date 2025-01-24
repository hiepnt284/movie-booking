package com.hiepnt.moviebooking.controller;

import com.hiepnt.moviebooking.dto.response.MovieStatsDTO;
import com.hiepnt.moviebooking.service.RevenueStatisticService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/stat")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RevenueStatisticController {
    RevenueStatisticService revenueStatisticService;

    @GetMapping("/daily")
    public Map<LocalDate, BigDecimal> getDailyRevenue(
            @RequestParam int month,
            @RequestParam int year,
            @RequestParam(required = false) Integer theaterId) {
        return revenueStatisticService.getDailyRevenue(month, year, theaterId);
    }

    @GetMapping("/monthly")
    public Map<Integer, BigDecimal> getMonthlyRevenue(
            @RequestParam int year,
            @RequestParam(required = false) Integer theaterId) {
        return revenueStatisticService.getMonthlyRevenue(year, theaterId);
    }

    @GetMapping("/yearly")
    public Map<Integer, BigDecimal> getYearlyRevenue(
            @RequestParam(required = false) Integer theaterId) {
        return revenueStatisticService.getYearlyRevenue(theaterId);
    }

    @GetMapping("/movie")
    public List<MovieStatsDTO> getMovieStats(
            @RequestParam(required = false) Integer theaterId
    ) {
        return revenueStatisticService.getMovieStats(theaterId);
    }
}
