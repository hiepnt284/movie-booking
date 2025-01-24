package com.hiepnt.moviebooking.service;

import com.hiepnt.moviebooking.dto.request.DayPriceDto;
import com.hiepnt.moviebooking.dto.request.SeatTypeCreationDto;
import com.hiepnt.moviebooking.dto.response.SeatTypeResponse;
import com.hiepnt.moviebooking.entity.DayPrice;
import com.hiepnt.moviebooking.entity.SeatType;
import com.hiepnt.moviebooking.exception.AppException;
import com.hiepnt.moviebooking.exception.ErrorCode;
import com.hiepnt.moviebooking.repository.DayPriceRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DayPriceService {

    DayPriceRepository dayPriceRepository;

    public List<DayPrice> getAll(){

        return dayPriceRepository.findAll();
    }

    public DayPrice update(int id, DayPriceDto dayPriceDto) {
        if(dayPriceDto==null) throw new AppException(ErrorCode.INVALID_INPUT_DATA);
        DayPrice dayPrice = dayPriceRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));
        dayPrice.setBasePrice(dayPriceDto.getBasePrice());
        return dayPriceRepository.save(dayPrice);
    }

}
