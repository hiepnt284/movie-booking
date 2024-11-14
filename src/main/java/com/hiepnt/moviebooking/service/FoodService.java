package com.hiepnt.moviebooking.service;

import com.hiepnt.moviebooking.common.PageResponse;
import com.hiepnt.moviebooking.dto.request.FoodCreationDto;
import com.hiepnt.moviebooking.dto.response.FoodResponse;
import com.hiepnt.moviebooking.entity.Food;
import com.hiepnt.moviebooking.exception.AppException;
import com.hiepnt.moviebooking.exception.ErrorCode;
import com.hiepnt.moviebooking.mapper.FoodMapper;
import com.hiepnt.moviebooking.provider.CloudinaryService;
import com.hiepnt.moviebooking.repository.FoodRepository;
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
public class FoodService {
    FoodRepository foodRepository;
    CloudinaryService cloudinaryService;
    FoodMapper foodMapper;

    public FoodResponse create(FoodCreationDto foodCreationDto, MultipartFile img) throws IOException {
        if(img==null || foodCreationDto==null) throw new AppException(ErrorCode.INVALID_INPUT_DATA);
        if(!Objects.requireNonNull(img.getContentType()).startsWith("image")) throw new AppException(ErrorCode.NOT_IMG);
        Food food = foodMapper.toEntity(foodCreationDto);
        String imgUrl = cloudinaryService.upload(img);
        food.setImg(imgUrl);
        return foodMapper.toResponse(foodRepository.save(food));
    }

    public List<FoodResponse> getAllForUser(){
        List<Food> foodList = foodRepository.findByIsActiveTrue();

        return foodList.stream()
                .map(foodMapper::toResponse)
                .toList();
    }

    public PageResponse<FoodResponse> getAllForAdmin(
            int page,
            int pageSize,
            String sortBy,
            String direction,
            String keyword
    ){
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.fromString(direction), sortBy));
        Page<Food> foodPage;
        if (keyword == null || keyword.trim().isEmpty()) {
            foodPage = foodRepository.findAll(pageable);
        }else{
            foodPage = foodRepository.findByTitleOrDescriptionContainingIgnoreCase(keyword, pageable);
        }
        List<FoodResponse> foodResponseList = foodPage.getContent().stream()
                .map(foodMapper::toResponse)
                .toList();
        return PageResponse.<FoodResponse>builder()
                .content(foodResponseList)
                .totalElements(foodPage.getTotalElements())
                .totalPages(foodPage.getTotalPages())
                .pageNo(foodPage.getNumber()+1)
                .pageSize(foodPage.getSize())
                .build();
    }


    public String delete(int id) throws IOException {
        Food food = foodRepository.findById(id)
                .orElseThrow(()->new AppException(ErrorCode.NOT_EXISTED));
        String url = food.getImg();
        foodRepository.deleteById(id);
        String idImg = cloudinaryService.extractPublicIdFromUrl(url);
        cloudinaryService.deleteFile(idImg);
        return "Delete food success";
    }

    public FoodResponse get(int id) {
        Food food = foodRepository.findById(id).orElseThrow(()->new AppException(ErrorCode.NOT_EXISTED));
        return foodMapper.toResponse(food);
    }

    public FoodResponse update(int id, FoodCreationDto foodCreationDto, MultipartFile img) throws IOException {
        if(foodCreationDto==null) throw new AppException(ErrorCode.INVALID_INPUT_DATA);
        Food food = foodRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));
        foodMapper.update(food,foodCreationDto);
        if(img!=null){
            if(!Objects.requireNonNull(img.getContentType()).startsWith("image")) throw new AppException(ErrorCode.NOT_IMG);
            cloudinaryService.deleteFile(cloudinaryService.extractPublicIdFromUrl(food.getImg()));
            String imgUrl = cloudinaryService.upload(img);
            food.setImg(imgUrl);
        }
        return foodMapper.toResponse(foodRepository.save(food));
    }
}
