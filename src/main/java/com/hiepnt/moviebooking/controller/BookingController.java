package com.hiepnt.moviebooking.controller;

import com.hiepnt.moviebooking.common.ApiResponse;
import com.hiepnt.moviebooking.dto.response.BookingResponse;
import com.hiepnt.moviebooking.service.BookingService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/booking")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookingController {
    BookingService bookingService;
    @GetMapping("/{id}")
    public ApiResponse<BookingResponse> get(@PathVariable int id){
        return ApiResponse
                .<BookingResponse>builder()
                .result(bookingService.get(id))
                .build();
    }
    @PostMapping("/verify")
    public ApiResponse<String> verifyQRCode(@RequestParam String bookingCode){
        return ApiResponse
                .<String>builder()
                .result(bookingService.verifyQRCode(bookingCode))
                .build();

    }
}
