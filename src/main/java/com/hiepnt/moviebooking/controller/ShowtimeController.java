package com.hiepnt.moviebooking.controller;

import com.hiepnt.moviebooking.common.ApiResponse;
import com.hiepnt.moviebooking.dto.request.ShowtimeCreationDto;
import com.hiepnt.moviebooking.dto.request.ShowtimeUpdateDto;
import com.hiepnt.moviebooking.dto.response.*;
import com.hiepnt.moviebooking.service.ShowtimeService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/showtime")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
//@SecurityRequirement(name = "bearerAuth")
//@Tag(name = "Tài khoản cá nhân", description = "APIs tài khoản cá nhân")
public class ShowtimeController {
    ShowtimeService showtimeService;

    // Tạo mới một suất chiếu
    @PostMapping
    public ApiResponse<Integer> create(
            @Valid @RequestBody ShowtimeCreationDto showtimeCreationDto
    ) {
        return ApiResponse
                .<Integer>builder()
                .result(showtimeService.create(showtimeCreationDto))
                .build();
    }

    // Lấy tất cả các suất chiếu theo rạp và ngày
    @GetMapping
    public ApiResponse<List<ShowtimeByRoomResponse>> getAll(
            @RequestParam int theaterId,
            @RequestParam LocalDate date
    ) {
        return ApiResponse
                .<List<ShowtimeByRoomResponse>>builder()
                .result(showtimeService.getAllByAdmin(theaterId, date))
                .build();
    }

    @GetMapping("/get")
    public ApiResponse<List<ShowtimeForUserResponse>> getAllForUser(
            @RequestParam int movieId,
            @RequestParam LocalDate date
    ) {
        return ApiResponse
                .<List<ShowtimeForUserResponse>>builder()
                .result(showtimeService.getAllByUser(movieId, date))
                .build();
    }

    @GetMapping("/available-dates")
    public ApiResponse<List<AvailableDateResponse>> getAvailableDates(@RequestParam int movieId) {
        return ApiResponse
                .<List<AvailableDateResponse>>builder()
                .result(showtimeService.getAvailableDatesForMovieFromToday(movieId))
                .build();
    }

    // Lấy chi tiết suất chiếu theo ID
    @GetMapping("/{showtimeId}")
    public ApiResponse<ShowtimeResponse> getById(@PathVariable int showtimeId) {
        return ApiResponse
                .<ShowtimeResponse>builder()
                .result(showtimeService.getById(showtimeId))
                .build();
    }

    // Cập nhật suất chiếu
    @PutMapping("/{showtimeId}")
    public ApiResponse<Void> updateShowtime(
            @PathVariable int showtimeId,
            @Valid @RequestBody ShowtimeUpdateDto showtimeUpdateDto
    ) {
        showtimeService.updateShowtime(showtimeId, showtimeUpdateDto);
        return ApiResponse.<Void>builder().build();
    }

    // Xóa suất chiếu theo ID
    @DeleteMapping("/{showtimeId}")
    public ApiResponse<Void> deleteShowtime(@PathVariable int showtimeId) {
        showtimeService.deleteShowtime(showtimeId);
        return ApiResponse.<Void>builder().build();
    }

    @GetMapping("/select/{showtimeId}")
    public ApiResponse<SelectAShowTimeResponse> getDataWhenSelectAShowtime(@PathVariable int showtimeId){
        return ApiResponse
                .<SelectAShowTimeResponse>builder()
                .result(showtimeService.getDataWhenSelectAShowtime(showtimeId))
                .build();
    }
}
