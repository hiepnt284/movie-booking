package com.hiepnt.moviebooking.service;

import com.hiepnt.moviebooking.common.PageResponse;
import com.hiepnt.moviebooking.dto.request.PostDto;
import com.hiepnt.moviebooking.dto.response.PostResponse;
import com.hiepnt.moviebooking.entity.Post;
import com.hiepnt.moviebooking.exception.AppException;
import com.hiepnt.moviebooking.exception.ErrorCode;
import com.hiepnt.moviebooking.mapper.PostMapper;
import com.hiepnt.moviebooking.provider.CloudinaryService;
import com.hiepnt.moviebooking.repository.PostRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostService {
    PostRepository postRepository;
    CloudinaryService cloudinaryService;
    PostMapper postMapper;

    public String uploadImage(MultipartFile thumbnail) throws IOException {
        if(thumbnail==null) throw new AppException(ErrorCode.INVALID_INPUT_DATA);
        if(!Objects.requireNonNull(thumbnail.getContentType()).startsWith("image")) throw new AppException(ErrorCode.NOT_IMG);
        return cloudinaryService.upload(thumbnail);
    }

    public PostResponse create(PostDto postDto, MultipartFile thumbnail) throws IOException {
        if(postDto==null) throw new AppException(ErrorCode.INVALID_INPUT_DATA);
        Post post = postMapper.toEntity(postDto);
        if(thumbnail!=null){
            if(!Objects.requireNonNull(thumbnail.getContentType()).startsWith("image")) throw new AppException(ErrorCode.NOT_IMG);
            String thumbnailUrl = cloudinaryService.upload(thumbnail);
            post.setThumbnail(thumbnailUrl);
        }
        return postMapper.toResponse(postRepository.save(post));
    }

    public List<PostResponse> getAllForUser(){
        List<Post> postList = postRepository.findByIsActiveTrue();

        return postList.stream()
                .map(postMapper::toResponse)
                .toList();
    }

    public PageResponse<PostResponse> getAllForAdmin(
            int page,
            int pageSize,
            String sortBy,
            String direction,
            String keyword
    ){
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.fromString(direction), sortBy));
        Page<Post> postPage;
        if (keyword == null || keyword.trim().isEmpty()) {
            postPage = postRepository.findAll(pageable);
        }else{
            postPage = postRepository.findByTitleOrDescriptionContainingIgnoreCase(keyword, pageable);
        }
        List<PostResponse> postResponseList = postPage.getContent().stream()
                .map(postMapper::toResponse)
                .toList();
        return PageResponse.<PostResponse>builder()
                .content(postResponseList)
                .totalElements(postPage.getTotalElements())
                .totalPages(postPage.getTotalPages())
                .pageNo(postPage.getNumber()+1)
                .pageSize(postPage.getSize())
                .build();
    }


    public String delete(int id) throws IOException {
        Post post = postRepository.findById(id)
                .orElseThrow(()->new AppException(ErrorCode.NOT_EXISTED));
        String url = post.getThumbnail();
        postRepository.deleteById(id);
        if(url!=null){
            String idImg = cloudinaryService.extractPublicIdFromUrl(url);
            cloudinaryService.deleteFile(idImg);
        }
        return "Delete post success";
    }

    public PostResponse get(int id) {
        Post post = postRepository.findById(id).orElseThrow(()->new AppException(ErrorCode.NOT_EXISTED));
        return postMapper.toResponse(post);
    }

    public PostResponse update(int id, PostDto postDto, MultipartFile thumbnail) throws IOException {
        if(postDto==null) throw new AppException(ErrorCode.INVALID_INPUT_DATA);
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));
        postMapper.update(post,postDto);
        if(thumbnail!=null){
            if(!Objects.requireNonNull(thumbnail.getContentType()).startsWith("image")) throw new AppException(ErrorCode.NOT_IMG);
            if(post.getThumbnail()!=null){
                cloudinaryService.deleteFile(cloudinaryService.extractPublicIdFromUrl(post.getThumbnail()));
            }
            String thumbnailUrl = cloudinaryService.upload(thumbnail);
            post.setThumbnail(thumbnailUrl);
        }
        return postMapper.toResponse(postRepository.save(post));
    }
}
