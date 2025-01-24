package com.hiepnt.moviebooking.service;

import com.hiepnt.moviebooking.common.PageResponse;
import com.hiepnt.moviebooking.dto.request.SeatTypeCreationDto;
import com.hiepnt.moviebooking.dto.response.SeatTypeResponse;
import com.hiepnt.moviebooking.entity.SeatType;
import com.hiepnt.moviebooking.exception.AppException;
import com.hiepnt.moviebooking.exception.ErrorCode;
import com.hiepnt.moviebooking.mapper.SeatTypeMapper;
import com.hiepnt.moviebooking.repository.SeatTypeRepository;
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
public class SeatTypeService {
    SeatTypeRepository seatTypeRepository;
    SeatTypeMapper seatTypeMapper;

    public SeatTypeResponse create(SeatTypeCreationDto seatTypeCreationDto) {
        if(seatTypeCreationDto==null) throw new AppException(ErrorCode.INVALID_INPUT_DATA);
        if(seatTypeRepository.existsByName(seatTypeCreationDto.getName())) throw new AppException(ErrorCode.EXISTED);
        SeatType seatType = seatTypeMapper.toEntity(seatTypeCreationDto);
        return seatTypeMapper.toResponse(seatTypeRepository.save(seatType));
    }

    public List<SeatTypeResponse> getAll(){
        List<SeatType> seatTypeList = seatTypeRepository.findAll();

        return seatTypeList.stream()
                .map(seatTypeMapper::toResponse)
                .toList();
    }

    public PageResponse<SeatTypeResponse> getAllForAdmin(
            int page,
            int pageSize,
            String sortBy,
            String direction,
            String keyword
    ){
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.fromString(direction), sortBy));
        Page<SeatType> seatTypePage;
        if (keyword == null || keyword.trim().isEmpty()) {
            seatTypePage = seatTypeRepository.findAll(pageable);
        }else{
            seatTypePage = seatTypeRepository.findByNameContainingIgnoreCase(keyword, pageable);
        }
        List<SeatTypeResponse> seatTypeResponseList = seatTypePage.getContent().stream()
                .map(seatTypeMapper::toResponse)
                .toList();
        return PageResponse.<SeatTypeResponse>builder()
                .content(seatTypeResponseList)
                .totalElements(seatTypePage.getTotalElements())
                .totalPages(seatTypePage.getTotalPages())
                .pageNo(seatTypePage.getNumber()+1)
                .pageSize(seatTypePage.getSize())
                .build();
    }


    public String delete(int id) {
        SeatType seatType = seatTypeRepository.findById(id)
                .orElseThrow(()->new AppException(ErrorCode.NOT_EXISTED));
        seatTypeRepository.deleteById(id);
        return "Delete success";
    }

    public SeatTypeResponse get(int id) {
        SeatType seatType = seatTypeRepository.findById(id).orElseThrow(()->new AppException(ErrorCode.NOT_EXISTED));
        return seatTypeMapper.toResponse(seatType);
    }

    public SeatTypeResponse update(int id, SeatTypeCreationDto seatTypeCreationDto) {
        if(seatTypeCreationDto==null) throw new AppException(ErrorCode.INVALID_INPUT_DATA);
        SeatType seatType = seatTypeRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));
        if(seatTypeRepository.existsByName(seatTypeCreationDto.getName())
                && !seatTypeCreationDto.getName().equals(seatType.getName())) throw new AppException(ErrorCode.EXISTED);
        seatTypeMapper.update(seatType,seatTypeCreationDto);
        return seatTypeMapper.toResponse(seatTypeRepository.save(seatType));
    }
}
