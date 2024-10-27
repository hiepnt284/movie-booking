package com.hiepnt.moviebooking.service;

import com.hiepnt.moviebooking.common.PageResponse;
import com.hiepnt.moviebooking.dto.request.TheaterCreationDto;
import com.hiepnt.moviebooking.dto.response.TheaterResponse;
import com.hiepnt.moviebooking.entity.Theater;
import com.hiepnt.moviebooking.exception.AppException;
import com.hiepnt.moviebooking.exception.ErrorCode;
import com.hiepnt.moviebooking.mapper.TheaterMapper;
import com.hiepnt.moviebooking.provider.CloudinaryService;
import com.hiepnt.moviebooking.repository.TheaterRepository;
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
public class TheaterService {
    TheaterRepository theaterRepository;
    TheaterMapper theaterMapper;
    CloudinaryService cloudinaryService;

    public TheaterResponse create(TheaterCreationDto theaterCreationDto, MultipartFile img) throws IOException {
        if(img==null || theaterCreationDto==null) throw new AppException(ErrorCode.INVALID_INPUT_DATA);
        if(!Objects.requireNonNull(img.getContentType()).startsWith("image")) throw new AppException(ErrorCode.NOT_IMG);
        if(theaterRepository.existsByName(theaterCreationDto.getName())) throw new AppException(ErrorCode.EXISTED);
        Theater theater = theaterMapper.toEntity(theaterCreationDto);
        String imgUrl = cloudinaryService.upload(img);
        theater.setImg(imgUrl);
        return theaterMapper.toResponse(theaterRepository.save(theater));
    }

    public List<TheaterResponse> getAll(){
        List<Theater> theaterList = theaterRepository.findAll();

        return theaterList.stream()
                .map(theaterMapper::toResponse)
                .toList();
    }

    public PageResponse<TheaterResponse> getAllForAdmin(
            int page,
            int pageSize,
            String sortBy,
            String direction,
            String keyword
    ){
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.fromString(direction), sortBy));
        Page<Theater> theaterPage;
        if (keyword == null || keyword.trim().isEmpty()) {
            theaterPage = theaterRepository.findAll(pageable);
        }else{
            theaterPage = theaterRepository.findByTitleOrDescriptionContainingIgnoreCase(keyword, pageable);
        }
        List<TheaterResponse> theaterResponseList = theaterPage.getContent().stream()
                .map(theaterMapper::toResponse)
                .toList();
        return PageResponse.<TheaterResponse>builder()
                .content(theaterResponseList)
                .totalElements(theaterPage.getTotalElements())
                .totalPages(theaterPage.getTotalPages())
                .pageNo(theaterPage.getNumber()+1)
                .pageSize(theaterPage.getSize())
                .build();
    }


    public String delete(int id) throws IOException {
        Theater theater = theaterRepository.findById(id)
                .orElseThrow(()->new AppException(ErrorCode.NOT_EXISTED));
        String url = theater.getImg();
        theaterRepository.deleteById(id);
        String idPoster = cloudinaryService.extractPublicIdFromUrl(url);
        cloudinaryService.deleteFile(idPoster);
        return "Delete success";
    }

    public TheaterResponse get(int id) {
        Theater theater = theaterRepository.findById(id).orElseThrow(()->new AppException(ErrorCode.NOT_EXISTED));
        return theaterMapper.toResponse(theater);
    }

    public TheaterResponse update(int id, TheaterCreationDto theaterCreationDto, MultipartFile img) throws IOException {
        if(theaterCreationDto==null) throw new AppException(ErrorCode.INVALID_INPUT_DATA);
        Theater theater = theaterRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));
        if(theaterRepository.existsByName(theaterCreationDto.getName())
                && !theaterCreationDto.getName().equals(theater.getName())) throw new AppException(ErrorCode.EXISTED);
        theaterMapper.updateEntity(theater,theaterCreationDto);
        if(img!=null){
            if(!Objects.requireNonNull(img.getContentType()).startsWith("image")) throw new AppException(ErrorCode.NOT_IMG);
            cloudinaryService.deleteFile(cloudinaryService.extractPublicIdFromUrl(theater.getImg()));
            String posterUrl = cloudinaryService.upload(img);
            theater.setImg(posterUrl);
        }
        return theaterMapper.toResponse(theaterRepository.save(theater));
    }


}
