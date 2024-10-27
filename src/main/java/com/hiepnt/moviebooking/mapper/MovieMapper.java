package com.hiepnt.moviebooking.mapper;

import com.hiepnt.moviebooking.dto.request.MovieCreationDto;
import com.hiepnt.moviebooking.dto.response.MovieResponse;
import com.hiepnt.moviebooking.entity.Movie;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MovieMapper {
    @Mapping(target = "poster", ignore = true)
    Movie toMovie(MovieCreationDto request);

    MovieResponse toMovieResponse(Movie movie);
    void updateMovie(@MappingTarget Movie movie, MovieCreationDto request);
}
