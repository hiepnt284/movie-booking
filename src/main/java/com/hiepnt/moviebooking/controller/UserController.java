package com.hiepnt.moviebooking.controller;

import com.hiepnt.moviebooking.common.ApiResponse;
import com.hiepnt.moviebooking.common.PageResponse;
import com.hiepnt.moviebooking.dto.request.UpdatePasswordDto;
import com.hiepnt.moviebooking.dto.response.BookingItemResponse;
import com.hiepnt.moviebooking.dto.response.UserResponse;
import com.hiepnt.moviebooking.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
//@SecurityRequirement(name = "bearerAuth")
//@Tag(name = "Tài khoản cá nhân", description = "APIs tài khoản cá nhân")
public class UserController {
    UserService userService;

    @GetMapping("/info")
    public ApiResponse<UserResponse> getInfo(){
        return ApiResponse
                .<UserResponse>builder()
                .result(userService.getInfo())
                .build();
    }

    @Operation(summary = "Đổi mật khẩu", description = "Đổi mật khẩu của người dùng hiện tại")
    @PutMapping("/change-password")
    public ApiResponse<Void> changePassword(
            @Valid
            @RequestBody UpdatePasswordDto request
    ) {
        return ApiResponse.<Void>builder()
                .result(userService.changePassword(request))
                .build();
    }

    @GetMapping("booking-history")
    public ApiResponse<List<BookingItemResponse>> getBookingHistory(){
        return ApiResponse
                .<List<BookingItemResponse>>builder()
                .result(userService.getBookingHistory())
                .build();
    }

    @GetMapping
    public ApiResponse<PageResponse<UserResponse>> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) String keyword
    ){
        return ApiResponse
                .<PageResponse<UserResponse>>builder()
                .result(userService.getAll(page, pageSize, sortBy, direction, keyword))
                .build();
    }

    @PatchMapping("/{id}")
    public ApiResponse<String> changStatus(
            @PathVariable int id
    ) {
        return ApiResponse
                .<String>builder()
                .result(userService.changeStatus(id))
                .build();
    }
}
