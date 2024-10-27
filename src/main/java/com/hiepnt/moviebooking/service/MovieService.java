package com.hiepnt.moviebooking.service;

import com.hiepnt.moviebooking.common.PageResponse;
import com.hiepnt.moviebooking.dto.request.MovieCreationDto;
import com.hiepnt.moviebooking.dto.response.MovieResponse;
import com.hiepnt.moviebooking.entity.Movie;
import com.hiepnt.moviebooking.exception.AppException;
import com.hiepnt.moviebooking.exception.ErrorCode;
import com.hiepnt.moviebooking.mapper.MovieMapper;
import com.hiepnt.moviebooking.provider.CloudinaryService;
import com.hiepnt.moviebooking.repository.MovieRepository;
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
public class MovieService {
    MovieRepository movieRepository;
    CloudinaryService cloudinaryService;
    MovieMapper movieMapper;

    public MovieResponse createMovie(MovieCreationDto movieCreationDto, MultipartFile poster) throws IOException {
        if(poster==null || movieCreationDto==null) throw new AppException(ErrorCode.INVALID_INPUT_DATA);
        if(!Objects.requireNonNull(poster.getContentType()).startsWith("image")) throw new AppException(ErrorCode.NOT_IMG);
        if(movieRepository.existsByTitle(movieCreationDto.getTitle())) throw new AppException(ErrorCode.EXISTED);
        Movie movie = movieMapper.toMovie(movieCreationDto);
        String posterUrl = cloudinaryService.upload(poster);
        movie.setPoster(posterUrl);
        return movieMapper.toMovieResponse(movieRepository.save(movie));
    }

    public List<MovieResponse> getAllMovieForUser(){
        List<Movie> movieList = movieRepository.findByIsActiveTrue();

        return movieList.stream()
                .map(movieMapper::toMovieResponse)
                .toList();
    }

    public PageResponse<MovieResponse> getAllMovieForAdmin(
            int page,
            int pageSize,
            String sortBy,
            String direction,
            String keyword
    ){
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.fromString(direction), sortBy));
        Page<Movie> moviePage;
        if (keyword == null || keyword.trim().isEmpty()) {
            moviePage = movieRepository.findAll(pageable);
        }else{
            moviePage = movieRepository.findByTitleOrDescriptionContainingIgnoreCase(keyword, pageable);
        }
        List<MovieResponse> movieResponseList = moviePage.getContent().stream()
                .map(movieMapper::toMovieResponse)
                .toList();
        return PageResponse.<MovieResponse>builder()
                .content(movieResponseList)
                .totalElements(moviePage.getTotalElements())
                .totalPages(moviePage.getTotalPages())
                .pageNo(moviePage.getNumber()+1)
                .pageSize(moviePage.getSize())
                .build();
    }


    public String deleteMovie(int id) throws IOException {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(()->new AppException(ErrorCode.NOT_EXISTED));
        String url = movie.getPoster();
        movieRepository.deleteById(id);
        String idPoster = cloudinaryService.extractPublicIdFromUrl(url);
        cloudinaryService.deleteFile(idPoster);
        return "Delete movie success";
    }

    public MovieResponse getAMovie(int id) {
        Movie movie = movieRepository.findById(id).orElseThrow(()->new AppException(ErrorCode.NOT_EXISTED));
        return movieMapper.toMovieResponse(movie);
    }

    public MovieResponse updateMovie(int id, MovieCreationDto movieCreationDto, MultipartFile poster) throws IOException {
        if(movieCreationDto==null) throw new AppException(ErrorCode.INVALID_INPUT_DATA);
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));
        if(movieRepository.existsByTitle(movieCreationDto.getTitle())
                && !movieCreationDto.getTitle().equals(movie.getTitle())) throw new AppException(ErrorCode.EXISTED);
        movieMapper.updateMovie(movie,movieCreationDto);
        if(poster!=null){
            if(!Objects.requireNonNull(poster.getContentType()).startsWith("image")) throw new AppException(ErrorCode.NOT_IMG);
            cloudinaryService.deleteFile(cloudinaryService.extractPublicIdFromUrl(movie.getPoster()));
            String posterUrl = cloudinaryService.upload(poster);
            movie.setPoster(posterUrl);
        }
        return movieMapper.toMovieResponse(movieRepository.save(movie));
    }
}
