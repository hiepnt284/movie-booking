package com.hiepnt.moviebooking.mapper;

import com.hiepnt.moviebooking.dto.request.FoodCreationDto;
import com.hiepnt.moviebooking.dto.response.FoodResponse;
import com.hiepnt.moviebooking.entity.Food;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface FoodMapper {
    @Mapping(target = "img", ignore = true)
    Food toEntity(FoodCreationDto request);

    FoodResponse toResponse(Food food);
    void update(@MappingTarget Food food, FoodCreationDto request);
}
