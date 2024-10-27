package com.hiepnt.moviebooking.service;

import com.hiepnt.moviebooking.common.PageResponse;
import com.hiepnt.moviebooking.dto.request.RoomTypeCreationDto;
import com.hiepnt.moviebooking.dto.response.RoomTypeResponse;
import com.hiepnt.moviebooking.entity.RoomType;
import com.hiepnt.moviebooking.exception.AppException;
import com.hiepnt.moviebooking.exception.ErrorCode;
import com.hiepnt.moviebooking.mapper.RoomTypeMapper;
import com.hiepnt.moviebooking.repository.RoomTypeRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoomTypeService {
    RoomTypeRepository roomTypeRepository;
    RoomTypeMapper roomTypeMapper;

    public RoomTypeResponse create(RoomTypeCreationDto roomTypeCreationDto) {
        if(roomTypeCreationDto==null) throw new AppException(ErrorCode.INVALID_INPUT_DATA);
        if(roomTypeRepository.existsByName(roomTypeCreationDto.getName())) throw new AppException(ErrorCode.EXISTED);
        RoomType roomType = roomTypeMapper.toEntity(roomTypeCreationDto);
        return roomTypeMapper.toResponse(roomTypeRepository.save(roomType));
    }

    public List<RoomTypeResponse> getAll(){
        List<RoomType> roomTypeList = roomTypeRepository.findAll();

        return roomTypeList.stream()
                .map(roomTypeMapper::toResponse)
                .toList();
    }

    public PageResponse<RoomTypeResponse> getAllForAdmin(
            int page,
            int pageSize,
            String sortBy,
            String direction,
            String keyword
    ){
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.fromString(direction), sortBy));
        Page<RoomType> roomTypePage;
        if (keyword == null || keyword.trim().isEmpty()) {
            roomTypePage = roomTypeRepository.findAll(pageable);
        }else{
            roomTypePage = roomTypeRepository.findByNameContainingIgnoreCase(keyword, pageable);
        }
        List<RoomTypeResponse> roomTypeResponseList = roomTypePage.getContent().stream()
                .map(roomTypeMapper::toResponse)
                .toList();
        return PageResponse.<RoomTypeResponse>builder()
                .content(roomTypeResponseList)
                .totalElements(roomTypePage.getTotalElements())
                .totalPages(roomTypePage.getTotalPages())
                .pageNo(roomTypePage.getNumber()+1)
                .pageSize(roomTypePage.getSize())
                .build();
    }


    public String delete(int id) {
        RoomType roomType = roomTypeRepository.findById(id)
                .orElseThrow(()->new AppException(ErrorCode.NOT_EXISTED));
        roomTypeRepository.deleteById(id);
        return "Delete success";
    }

    public RoomTypeResponse get(int id) {
        RoomType roomType = roomTypeRepository.findById(id).orElseThrow(()->new AppException(ErrorCode.NOT_EXISTED));
        return roomTypeMapper.toResponse(roomType);
    }

    public RoomTypeResponse update(int id, RoomTypeCreationDto roomTypeCreationDto) {
        if(roomTypeCreationDto==null) throw new AppException(ErrorCode.INVALID_INPUT_DATA);
        RoomType roomType = roomTypeRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));
        if(roomTypeRepository.existsByName(roomTypeCreationDto.getName())
                && !roomTypeCreationDto.getName().equals(roomType.getName())) throw new AppException(ErrorCode.EXISTED);
        roomTypeMapper.update(roomType,roomTypeCreationDto);
        return roomTypeMapper.toResponse(roomTypeRepository.save(roomType));
    }
}
