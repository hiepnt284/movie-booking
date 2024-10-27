package com.hiepnt.moviebooking.mapper;

import com.hiepnt.moviebooking.dto.request.RoomTypeCreationDto;
import com.hiepnt.moviebooking.dto.response.RoomTypeResponse;
import com.hiepnt.moviebooking.entity.RoomType;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RoomTypeMapper {

    RoomType toEntity(RoomTypeCreationDto request);

    RoomTypeResponse toResponse(RoomType request);
    void update(@MappingTarget RoomType roomType, RoomTypeCreationDto request);
}
