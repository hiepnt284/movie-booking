package com.hiepnt.moviebooking.controller;

import com.google.zxing.WriterException;
import com.hiepnt.moviebooking.common.ApiResponse;
import com.hiepnt.moviebooking.dto.request.BookingRequest;
import com.hiepnt.moviebooking.dto.response.BookingResponse;
import com.hiepnt.moviebooking.payment.vnpay.VNPayResponse;
import com.hiepnt.moviebooking.service.BookingService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

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
    public ApiResponse<BookingResponse> verifyQRCode(@RequestParam String bookingCode){
        return ApiResponse
                .<BookingResponse>builder()
                .result(bookingService.verifyQRCode(bookingCode))
                .build();

    }

    @PostMapping("/book-off")
    public ApiResponse<BookingResponse> pay(@RequestBody BookingRequest bookingRequest) {
        return ApiResponse
                .<BookingResponse>builder()
                .result(bookingService.bookingOff(bookingRequest))
                .build();

    }
}
