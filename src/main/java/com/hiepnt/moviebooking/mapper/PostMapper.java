package com.hiepnt.moviebooking.mapper;

import com.hiepnt.moviebooking.dto.request.PostDto;
import com.hiepnt.moviebooking.dto.response.PostResponse;
import com.hiepnt.moviebooking.entity.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
@Mapper(componentModel = "spring")
public interface PostMapper {
    @Mapping(target = "thumbnail", ignore = true)
    Post toEntity(PostDto request);

    PostResponse toResponse(Post post);
    void update(@MappingTarget Post post, PostDto request);
}
