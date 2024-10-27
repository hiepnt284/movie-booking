package com.hiepnt.moviebooking.controller;

import com.hiepnt.moviebooking.common.ApiResponse;
import com.hiepnt.moviebooking.common.PageResponse;
import com.hiepnt.moviebooking.dto.request.SeatTypeCreationDto;
import com.hiepnt.moviebooking.dto.response.SeatTypeResponse;
import com.hiepnt.moviebooking.service.SeatTypeService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/seatype")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
//@SecurityRequirement(name = "bearerAuth")
//@Tag(name = "Tài khoản cá nhân", description = "APIs tài khoản cá nhân")
public class SeatTypeController {
    SeatTypeService seatTypeService;
    @PostMapping
    public ApiResponse<SeatTypeResponse> create(
            @Valid @RequestBody SeatTypeCreationDto seatTypeCreationDto
    ) {
        return ApiResponse
                .<SeatTypeResponse>builder()
                .result(seatTypeService.create(seatTypeCreationDto))
                .build();
    }

    @GetMapping("/get")
    public ApiResponse<List<SeatTypeResponse>> getAllForUser(){
        return ApiResponse
                .<List<SeatTypeResponse>>builder()
                .result(seatTypeService.getAll())
                .build();
    }

    @GetMapping
    public ApiResponse<PageResponse<SeatTypeResponse>> getAllForAdmin(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) String keyword
    ){
        return ApiResponse
                .<PageResponse<SeatTypeResponse>>builder()
                .result(seatTypeService.getAllForAdmin(page, pageSize, sortBy, direction,keyword))
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<SeatTypeResponse> get(@PathVariable int id){
        return ApiResponse
                .<SeatTypeResponse>builder()
                .result(seatTypeService.get(id))
                .build();
    }
    @PutMapping("/{id}")
    public ApiResponse<SeatTypeResponse> update(
            @PathVariable int id,
            @Valid @RequestBody SeatTypeCreationDto seatTypeCreationDto
    ) {
        return ApiResponse
                .<SeatTypeResponse>builder()
                .result(seatTypeService.update(id, seatTypeCreationDto))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable int id) {
        return ApiResponse
                .<Void>builder()
                .message(seatTypeService.delete(id))
                .build();
    }
}
