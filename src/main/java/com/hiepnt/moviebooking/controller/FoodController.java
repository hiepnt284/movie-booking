package com.hiepnt.moviebooking.controller;

import com.hiepnt.moviebooking.common.ApiResponse;
import com.hiepnt.moviebooking.common.PageResponse;
import com.hiepnt.moviebooking.dto.request.FoodCreationDto;
import com.hiepnt.moviebooking.dto.response.FoodResponse;
import com.hiepnt.moviebooking.service.FoodService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/food")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FoodController {
    FoodService foodService;
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<FoodResponse> create(
            @Valid @RequestPart(name = "food", required = false) FoodCreationDto foodCreationDto,
            @RequestPart(name = "img", required = false) MultipartFile img
    ) throws IOException {
        return ApiResponse
                .<FoodResponse>builder()
                .result(foodService.create(foodCreationDto, img))
                .build();
    }

    @GetMapping("/get")
    public ApiResponse<List<FoodResponse>> getAllForUser(){
        return ApiResponse
                .<List<FoodResponse>>builder()
                .result(foodService.getAllForUser())
                .build();
    }

    @GetMapping
    public ApiResponse<PageResponse<FoodResponse>> getAllForAdmin(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) String keyword
    ){
        return ApiResponse
                .<PageResponse<FoodResponse>>builder()
                .result(foodService.getAllForAdmin(page, pageSize, sortBy, direction,keyword))
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<FoodResponse> get(@PathVariable int id){
        return ApiResponse
                .<FoodResponse>builder()
                .result(foodService.get(id))
                .build();
    }
    @PutMapping("/{id}")
    public ApiResponse<FoodResponse> update(
            @PathVariable int id,
            @Valid @RequestPart(name = "food", required = false) FoodCreationDto foodCreationDto,
            @RequestPart(name = "img", required = false) MultipartFile img
    ) throws IOException {
        return ApiResponse
                .<FoodResponse>builder()
                .result(foodService.update(id, foodCreationDto, img))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable int id) throws IOException {
        return ApiResponse
                .<Void>builder()
                .message(foodService.delete(id))
                .build();
    }
}
