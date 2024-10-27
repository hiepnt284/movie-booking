package com.hiepnt.moviebooking.mapper;

import com.hiepnt.moviebooking.dto.request.TheaterCreationDto;
import com.hiepnt.moviebooking.dto.response.TheaterResponse;
import com.hiepnt.moviebooking.entity.Theater;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TheaterMapper {
    @Mapping(target = "img", ignore = true)
    Theater toEntity(TheaterCreationDto request);

    TheaterResponse toResponse(Theater request);
    void updateEntity(@MappingTarget Theater theater, TheaterCreationDto request);
}
