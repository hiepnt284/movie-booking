package com.hiepnt.moviebooking.service;

import com.hiepnt.moviebooking.common.PageResponse;
import com.hiepnt.moviebooking.dto.request.CarouselDto;
import com.hiepnt.moviebooking.dto.response.CarouselResponse;
import com.hiepnt.moviebooking.entity.Carousel;
import com.hiepnt.moviebooking.exception.AppException;
import com.hiepnt.moviebooking.exception.ErrorCode;
import com.hiepnt.moviebooking.mapper.CarouselMapper;
import com.hiepnt.moviebooking.provider.CloudinaryService;
import com.hiepnt.moviebooking.repository.CarouselRepository;
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
public class CarouselService {
    CarouselRepository carouselRepository;
    CloudinaryService cloudinaryService;
    CarouselMapper carouselMapper;

    public CarouselResponse create(CarouselDto carouselDto, MultipartFile img) throws IOException {
        if(img==null || carouselDto==null) throw new AppException(ErrorCode.INVALID_INPUT_DATA);
        if(!Objects.requireNonNull(img.getContentType()).startsWith("image")) throw new AppException(ErrorCode.NOT_IMG);
        Carousel carousel = carouselMapper.toEntity(carouselDto);
        String imgUrl = cloudinaryService.upload(img);
        carousel.setImg(imgUrl);
        return carouselMapper.toResponse(carouselRepository.save(carousel));
    }

    public List<CarouselResponse> getAllForUser(){
        List<Carousel> carouselList = carouselRepository.findByIsActiveTrue();

        return carouselList.stream()
                .map(carouselMapper::toResponse)
                .toList();
    }

    public PageResponse<CarouselResponse> getAllForAdmin(
            int page,
            int pageSize,
            String sortBy,
            String direction,
            String keyword
    ){
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.fromString(direction), sortBy));
        Page<Carousel> carouselPage;
        if (keyword == null || keyword.trim().isEmpty()) {
            carouselPage = carouselRepository.findAll(pageable);
        }else{
            carouselPage = carouselRepository.findByTitleOrDescriptionContainingIgnoreCase(keyword, pageable);
        }
        List<CarouselResponse> carouselResponseList = carouselPage.getContent().stream()
                .map(carouselMapper::toResponse)
                .toList();
        return PageResponse.<CarouselResponse>builder()
                .content(carouselResponseList)
                .totalElements(carouselPage.getTotalElements())
                .totalPages(carouselPage.getTotalPages())
                .pageNo(carouselPage.getNumber()+1)
                .pageSize(carouselPage.getSize())
                .build();
    }


    public String delete(int id) throws IOException {
        Carousel carousel = carouselRepository.findById(id)
                .orElseThrow(()->new AppException(ErrorCode.NOT_EXISTED));
        String url = carousel.getImg();
        carouselRepository.deleteById(id);
        String idImg = cloudinaryService.extractPublicIdFromUrl(url);
        cloudinaryService.deleteFile(idImg);
        return "Delete carousel success";
    }

    public CarouselResponse get(int id) {
        Carousel carousel = carouselRepository.findById(id).orElseThrow(()->new AppException(ErrorCode.NOT_EXISTED));
        return carouselMapper.toResponse(carousel);
    }

    public CarouselResponse update(int id, CarouselDto carouselDto, MultipartFile img) throws IOException {
        if(carouselDto==null) throw new AppException(ErrorCode.INVALID_INPUT_DATA);
        Carousel carousel = carouselRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));
        carouselMapper.update(carousel,carouselDto);
        if(img!=null){
            if(!Objects.requireNonNull(img.getContentType()).startsWith("image")) throw new AppException(ErrorCode.NOT_IMG);
            cloudinaryService.deleteFile(cloudinaryService.extractPublicIdFromUrl(carousel.getImg()));
            String imgUrl = cloudinaryService.upload(img);
            carousel.setImg(imgUrl);
        }
        return carouselMapper.toResponse(carouselRepository.save(carousel));
    }
}
