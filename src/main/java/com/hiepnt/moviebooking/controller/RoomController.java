package com.hiepnt.moviebooking.controller;

import com.hiepnt.moviebooking.common.ApiResponse;
import com.hiepnt.moviebooking.dto.request.RoomCreationDto;
import com.hiepnt.moviebooking.dto.request.RoomUpdateDto;
import com.hiepnt.moviebooking.dto.response.RoomResponse;
import com.hiepnt.moviebooking.service.RoomService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/room")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
//@SecurityRequirement(name = "bearerAuth")
//@Tag(name = "Tài khoản cá nhân", description = "APIs tài khoản cá nhân")
public class RoomController {
    RoomService roomService;
    @PostMapping
    public ApiResponse<RoomResponse> create(
            @Valid @RequestBody RoomCreationDto roomCreationDto
    ) {
        return ApiResponse
                .<RoomResponse>builder()
                .result(roomService.create(roomCreationDto))
                .build();
    }

    @GetMapping
    public ApiResponse<List<RoomResponse>> getAll(
            @RequestParam(value = "theaterId") Integer theaterId
    ) {
        return ApiResponse
                .<List<RoomResponse>>builder()
                .result(roomService.getAll(theaterId))
                .build();
    }
    @GetMapping("/{id}")
    public ApiResponse<RoomResponse> get(@PathVariable int id){
        return ApiResponse
                .<RoomResponse>builder()
                .result(roomService.get(id))
                .build();
    }
    @PutMapping("/{id}")
    public ApiResponse<RoomResponse> update(
            @PathVariable int id,
            @Valid @RequestBody RoomUpdateDto roomUpdateDto
    ) {
        return ApiResponse
                .<RoomResponse>builder()
                .result(roomService.update(id, roomUpdateDto))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable int id) {
        return ApiResponse
                .<Void>builder()
                .message(roomService.delete(id))
                .build();
    }
}
