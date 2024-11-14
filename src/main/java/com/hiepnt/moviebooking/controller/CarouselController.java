package com.hiepnt.moviebooking.controller;

import com.hiepnt.moviebooking.common.ApiResponse;
import com.hiepnt.moviebooking.common.PageResponse;
import com.hiepnt.moviebooking.dto.request.CarouselDto;
import com.hiepnt.moviebooking.dto.response.CarouselResponse;
import com.hiepnt.moviebooking.service.CarouselService;
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
@RequestMapping("/carousel")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CarouselController {
    CarouselService carouselService;
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<CarouselResponse> create(
            @Valid @RequestPart(name = "carousel", required = false) CarouselDto carouselDto,
            @RequestPart(name = "img", required = false) MultipartFile img
    ) throws IOException {
        return ApiResponse
                .<CarouselResponse>builder()
                .result(carouselService.create(carouselDto, img))
                .build();
    }

    @GetMapping("/get")
    public ApiResponse<List<CarouselResponse>> getAllForUser(){
        return ApiResponse
                .<List<CarouselResponse>>builder()
                .result(carouselService.getAllForUser())
                .build();
    }

    @GetMapping
    public ApiResponse<PageResponse<CarouselResponse>> getAllForAdmin(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) String keyword
    ){
        return ApiResponse
                .<PageResponse<CarouselResponse>>builder()
                .result(carouselService.getAllForAdmin(page, pageSize, sortBy, direction,keyword))
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<CarouselResponse> get(@PathVariable int id){
        return ApiResponse
                .<CarouselResponse>builder()
                .result(carouselService.get(id))
                .build();
    }
    @PutMapping("/{id}")
    public ApiResponse<CarouselResponse> update(
            @PathVariable int id,
            @Valid @RequestPart(name = "carousel", required = false) CarouselDto carouselDto,
            @RequestPart(name = "img", required = false) MultipartFile img
    ) throws IOException {
        return ApiResponse
                .<CarouselResponse>builder()
                .result(carouselService.update(id, carouselDto, img))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable int id) throws IOException {
        return ApiResponse
                .<Void>builder()
                .message(carouselService.delete(id))
                .build();
    }
}
