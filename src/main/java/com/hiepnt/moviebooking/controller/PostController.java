package com.hiepnt.moviebooking.controller;

import com.hiepnt.moviebooking.common.ApiResponse;
import com.hiepnt.moviebooking.common.PageResponse;
import com.hiepnt.moviebooking.dto.request.PostDto;
import com.hiepnt.moviebooking.dto.response.PostResponse;
import com.hiepnt.moviebooking.service.PostService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostController {
    PostService postService;
    @PostMapping("/upload-image")
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile thumbnail) throws IOException {
            String imageUrl = postService.uploadImage(thumbnail);
            return ResponseEntity.ok().body(Map.of("url", imageUrl));

    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<PostResponse> create(
            @Valid @RequestPart(name = "post", required = false) PostDto postDto,
            @RequestPart(name = "thumbnail", required = false) MultipartFile thumbnail
    ) throws IOException {
        return ApiResponse
                .<PostResponse>builder()
                .result(postService.create(postDto, thumbnail))
                .build();
    }

    @GetMapping("/get")
    public ApiResponse<List<PostResponse>> getAllForUser(){
        return ApiResponse
                .<List<PostResponse>>builder()
                .result(postService.getAllForUser())
                .build();
    }

    @GetMapping
    public ApiResponse<PageResponse<PostResponse>> getAllForAdmin(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) String keyword
    ){
        return ApiResponse
                .<PageResponse<PostResponse>>builder()
                .result(postService.getAllForAdmin(page, pageSize, sortBy, direction,keyword))
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<PostResponse> get(@PathVariable int id){
        return ApiResponse
                .<PostResponse>builder()
                .result(postService.get(id))
                .build();
    }
    @PutMapping("/{id}")
    public ApiResponse<PostResponse> update(
            @PathVariable int id,
            @Valid @RequestPart(name = "post", required = false) PostDto postDto,
            @RequestPart(name = "thumbnail", required = false) MultipartFile thumbnail
    ) throws IOException {
        return ApiResponse
                .<PostResponse>builder()
                .result(postService.update(id, postDto, thumbnail))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable int id) throws IOException {
        return ApiResponse
                .<Void>builder()
                .message(postService.delete(id))
                .build();
    }
}
