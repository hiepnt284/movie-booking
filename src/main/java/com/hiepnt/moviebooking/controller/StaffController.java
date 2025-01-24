package com.hiepnt.moviebooking.controller;

import com.hiepnt.moviebooking.common.ApiResponse;
import com.hiepnt.moviebooking.common.PageResponse;
import com.hiepnt.moviebooking.dto.request.StaffCreationDto;
import com.hiepnt.moviebooking.dto.response.UserResponse;
import com.hiepnt.moviebooking.service.StaffService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/staff")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StaffController {
    StaffService staffService;
    @PostMapping
    public ApiResponse<String> create(
            @Valid @RequestBody StaffCreationDto staffCreationDto
    ) {
        return ApiResponse
                .<String>builder()
                .result(staffService.create(staffCreationDto))
                .build();
    }

    @GetMapping
    public ApiResponse<PageResponse<UserResponse>> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer theaterId
    ){
        return ApiResponse
                .<PageResponse<UserResponse>>builder()
                .result(staffService.getAll(page, pageSize, sortBy, direction, theaterId, keyword))
                .build();
    }
    
    @GetMapping("/{id}")
    public ApiResponse<UserResponse> get(@PathVariable int id){
        return ApiResponse
                .<UserResponse>builder()
                .result(staffService.get(id))
                .build();
    }
    @PutMapping("/{id}")
    public ApiResponse<String> update(
            @PathVariable int id,
            @Valid @RequestBody StaffCreationDto staffCreationDto
    ) {
        return ApiResponse
                .<String>builder()
                .result(staffService.update(staffCreationDto, id))
                .build();
    }

    @PatchMapping("/{id}")
    public ApiResponse<String> changStatus(
            @PathVariable int id
    ) {
        return ApiResponse
                .<String>builder()
                .result(staffService.changeStatus(id))
                .build();
    }
}
