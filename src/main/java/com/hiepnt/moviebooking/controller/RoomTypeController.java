package com.hiepnt.moviebooking.controller;

import com.hiepnt.moviebooking.common.ApiResponse;
import com.hiepnt.moviebooking.common.PageResponse;
import com.hiepnt.moviebooking.dto.request.RoomTypeCreationDto;
import com.hiepnt.moviebooking.dto.response.RoomTypeResponse;
import com.hiepnt.moviebooking.service.RoomTypeService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/roomtype")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
//@SecurityRequirement(name = "bearerAuth")
//@Tag(name = "Tài khoản cá nhân", description = "APIs tài khoản cá nhân")
public class RoomTypeController {
    RoomTypeService roomTypeService;
    @PostMapping
    public ApiResponse<RoomTypeResponse> create(
            @Valid @RequestBody RoomTypeCreationDto roomTypeCreationDto
    ) {
        return ApiResponse
                .<RoomTypeResponse>builder()
                .result(roomTypeService.create(roomTypeCreationDto))
                .build();
    }

    @GetMapping("/get")
    public ApiResponse<List<RoomTypeResponse>> getAllForUser(){
        return ApiResponse
                .<List<RoomTypeResponse>>builder()
                .result(roomTypeService.getAll())
                .build();
    }

    @GetMapping
    public ApiResponse<PageResponse<RoomTypeResponse>> getAllForAdmin(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) String keyword
    ){
        return ApiResponse
                .<PageResponse<RoomTypeResponse>>builder()
                .result(roomTypeService.getAllForAdmin(page, pageSize, sortBy, direction,keyword))
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<RoomTypeResponse> get(@PathVariable int id){
        return ApiResponse
                .<RoomTypeResponse>builder()
                .result(roomTypeService.get(id))
                .build();
    }
    @PutMapping("/{id}")
    public ApiResponse<RoomTypeResponse> update(
            @PathVariable int id,
            @Valid @RequestBody RoomTypeCreationDto roomTypeCreationDto
    ) {
        return ApiResponse
                .<RoomTypeResponse>builder()
                .result(roomTypeService.update(id, roomTypeCreationDto))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable int id) {
        return ApiResponse
                .<Void>builder()
                .message(roomTypeService.delete(id))
                .build();
    }
}
