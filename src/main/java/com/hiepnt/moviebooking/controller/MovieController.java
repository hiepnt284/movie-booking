package com.hiepnt.moviebooking.controller;

import com.hiepnt.moviebooking.common.ApiResponse;
import com.hiepnt.moviebooking.common.PageResponse;
import com.hiepnt.moviebooking.dto.request.MovieCreationDto;
import com.hiepnt.moviebooking.dto.response.MovieResponse;
import com.hiepnt.moviebooking.service.MovieService;
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
@RequestMapping("/movie")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
//@SecurityRequirement(name = "bearerAuth")
//@Tag(name = "Tài khoản cá nhân", description = "APIs tài khoản cá nhân")
public class MovieController {

    MovieService movieService;
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<MovieResponse> createMovie(
            @Valid @RequestPart(name = "movie", required = false) MovieCreationDto movieCreationDto,
            @RequestPart(name = "poster", required = false) MultipartFile poster
    ) throws IOException {
        return ApiResponse
                .<MovieResponse>builder()
                .result(movieService.createMovie(movieCreationDto, poster))
                .build();
    }

    @GetMapping("/get")
    public ApiResponse<List<MovieResponse>> getAllMovieForUser(){
        return ApiResponse
                .<List<MovieResponse>>builder()
                .result(movieService.getAllMovieForUser())
                .build();
    }

    @GetMapping
    public ApiResponse<PageResponse<MovieResponse>> getAllMovieForAdmin(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) String keyword
    ){
        return ApiResponse
                .<PageResponse<MovieResponse>>builder()
                .result(movieService.getAllMovieForAdmin(page, pageSize, sortBy, direction,keyword))
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<MovieResponse> getAMovie(@PathVariable int id){
        return ApiResponse
                .<MovieResponse>builder()
                .result(movieService.getAMovie(id))
                .build();
    }
    @PutMapping("/{id}")
    public ApiResponse<MovieResponse> updateMovie(
            @PathVariable int id,
            @Valid @RequestPart(name = "movie", required = false) MovieCreationDto movieCreationDto,
            @RequestPart(name = "poster", required = false) MultipartFile poster
    ) throws IOException {
        return ApiResponse
                .<MovieResponse>builder()
                .result(movieService.updateMovie(id, movieCreationDto, poster))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteMovie(@PathVariable int id) throws IOException {
        return ApiResponse
                .<Void>builder()
                .message(movieService.deleteMovie(id))
                .build();
    }
}
