package com.hiepnt.moviebooking.controller;

import com.hiepnt.moviebooking.common.ApiResponse;
import com.hiepnt.moviebooking.dto.request.DayPriceDto;
import com.hiepnt.moviebooking.dto.request.SeatTypeCreationDto;
import com.hiepnt.moviebooking.entity.DayPrice;
import com.hiepnt.moviebooking.service.DayPriceService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dayprice")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DayPriceController {

    DayPriceService dayPriceService;

    @GetMapping
    public ApiResponse<List<DayPrice>> getAll(){
        return ApiResponse
                .<List<DayPrice>>builder()
                .result(dayPriceService.getAll())
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<DayPrice> update(
            @PathVariable int id,
            @Valid @RequestBody DayPriceDto dayPriceDto) {
        return ApiResponse
                .<DayPrice>builder()
                .result(dayPriceService.update(id, dayPriceDto))
                .build();
    }
}
