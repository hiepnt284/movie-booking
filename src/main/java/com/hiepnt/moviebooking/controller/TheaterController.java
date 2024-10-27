package com.hiepnt.moviebooking.controller;

import com.hiepnt.moviebooking.common.ApiResponse;
import com.hiepnt.moviebooking.common.PageResponse;
import com.hiepnt.moviebooking.dto.request.TheaterCreationDto;
import com.hiepnt.moviebooking.dto.response.TheaterResponse;
import com.hiepnt.moviebooking.service.TheaterService;
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
@RequestMapping("/theater")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
//@SecurityRequirement(name = "bearerAuth")
//@Tag(name = "Tài khoản cá nhân", description = "APIs tài khoản cá nhân")
public class TheaterController {

    TheaterService theaterService;
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<TheaterResponse> create(
            @Valid @RequestPart(name = "theater", required = false) TheaterCreationDto theaterCreationDto,
            @RequestPart(name = "img", required = false) MultipartFile img
    ) throws IOException {
        return ApiResponse
                .<TheaterResponse>builder()
                .result(theaterService.create(theaterCreationDto, img))
                .build();
    }

    @GetMapping("/get")
    public ApiResponse<List<TheaterResponse>> getAllForUser(){
        return ApiResponse
                .<List<TheaterResponse>>builder()
                .result(theaterService.getAll())
                .build();
    }

    @GetMapping
    public ApiResponse<PageResponse<TheaterResponse>> getAllForAdmin(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) String keyword
    ){
        return ApiResponse
                .<PageResponse<TheaterResponse>>builder()
                .result(theaterService.getAllForAdmin(page, pageSize, sortBy, direction,keyword))
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<TheaterResponse> get(@PathVariable int id){
        return ApiResponse
                .<TheaterResponse>builder()
                .result(theaterService.get(id))
                .build();
    }
    @PutMapping("/{id}")
    public ApiResponse<TheaterResponse> update(
            @PathVariable int id,
            @Valid @RequestPart(name = "theater", required = false) TheaterCreationDto theaterCreationDto,
            @RequestPart(name = "img", required = false) MultipartFile img
    ) throws IOException {
        return ApiResponse
                .<TheaterResponse>builder()
                .result(theaterService.update(id, theaterCreationDto, img))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable int id) throws IOException {
        return ApiResponse
                .<Void>builder()
                .message(theaterService.delete(id))
                .build();
    }
}
