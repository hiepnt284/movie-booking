package com.hiepnt.moviebooking.mapper;

import com.hiepnt.moviebooking.dto.request.CarouselDto;
import com.hiepnt.moviebooking.dto.response.CarouselResponse;
import com.hiepnt.moviebooking.entity.Carousel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CarouselMapper {
    @Mapping(target = "img", ignore = true)
    Carousel toEntity(CarouselDto request);

    CarouselResponse toResponse(Carousel carousel);
    void update(@MappingTarget Carousel carousel, CarouselDto request);
}
