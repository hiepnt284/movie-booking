package com.hiepnt.moviebooking.mapper;

import com.hiepnt.moviebooking.dto.request.SeatTypeCreationDto;
import com.hiepnt.moviebooking.dto.response.SeatTypeResponse;
import com.hiepnt.moviebooking.entity.SeatType;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SeatTypeMapper {
    SeatType toEntity(SeatTypeCreationDto request);

    SeatTypeResponse toResponse(SeatType request);
    void update(@MappingTarget SeatType seatType, SeatTypeCreationDto request);
}
