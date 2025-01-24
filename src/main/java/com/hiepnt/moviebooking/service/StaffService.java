package com.hiepnt.moviebooking.service;

import com.hiepnt.moviebooking.common.PageResponse;
import com.hiepnt.moviebooking.dto.request.StaffCreationDto;
import com.hiepnt.moviebooking.dto.response.UserResponse;
import com.hiepnt.moviebooking.entity.Theater;
import com.hiepnt.moviebooking.entity.User;
import com.hiepnt.moviebooking.entity.enums.Status;
import com.hiepnt.moviebooking.exception.AppException;
import com.hiepnt.moviebooking.exception.ErrorCode;
import com.hiepnt.moviebooking.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StaffService {
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;

    public String create(StaffCreationDto staffCreationDto){
        if (userRepository.existsByEmail(staffCreationDto.getEmail())) throw new AppException(ErrorCode.EXISTED);

        var staff = User.builder()
                .fullName(staffCreationDto.getFullName())
                .email(staffCreationDto.getEmail())
                .phoneNumber(staffCreationDto.getPhoneNumber())
                .gender(staffCreationDto.getGender())
                .dob(staffCreationDto.getDob())
                .isActive(staffCreationDto.getIsActive())
                .role(staffCreationDto.getRole())
                .theater(Theater.builder().id(staffCreationDto.getTheaterId()).build())
                .password(passwordEncoder.encode(staffCreationDto.getPassword()))
                .build();

        userRepository.save(staff);
        return "Success";
    }

    public String update(StaffCreationDto staffCreationDto, int id){
        var staff = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));
        staff.setRole(staffCreationDto.getRole());
        staff.setTheater(Theater.builder().id(staffCreationDto.getTheaterId()).build());
        staff.setPassword(passwordEncoder.encode(staffCreationDto.getPassword()));
        userRepository.save(staff);
        return "Success";
    }

    public String changeStatus(int id){
        var staff = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));
        staff.setIsActive(staff.getIsActive()==Status.ACTIVE?Status.BANNED:Status.ACTIVE);
        userRepository.save(staff);
        return "Success";
    }

    public UserResponse get(int id){
        var staff = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));
        return UserResponse.builder()
                .id(staff.getId())
                .email(staff.getEmail())
                .phoneNumber(staff.getPhoneNumber())
                .fullName(staff.getFullName())
                .gender(staff.getGender())
                .dob(staff.getDob())
                .avt(staff.getAvt())
                .role(staff.getRole())
                .theaterId(staff.getTheater() != null ? staff.getTheater().getId() : null)
                .isActive(staff.getIsActive())
                .build();
    }

    public PageResponse<UserResponse> getAll(
            int page,
            int pageSize,
            String sortBy,
            String direction,
            Integer theaterId,
            String keyword
    ){
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.fromString(direction), sortBy));
        Page<User> userPage = userRepository.findByEmailAndTheater(
                (keyword == null || keyword.trim().isEmpty()) ? null : keyword,
                theaterId,
                pageable
        );

        List<UserResponse> userResponseList = userPage.getContent().stream()
                .map(user -> UserResponse.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .phoneNumber(user.getPhoneNumber())
                        .fullName(user.getFullName())
                        .gender(user.getGender())
                        .dob(user.getDob())
                        .avt(user.getAvt())
                        .role(user.getRole())
                        .theaterId(user.getTheater() != null ? user.getTheater().getId() : null)
                        .isActive(user.getIsActive())
                        .build())
                .toList();

        return PageResponse.<UserResponse>builder()
                .content(userResponseList)
                .totalElements(userPage.getTotalElements())
                .totalPages(userPage.getTotalPages())
                .pageNo(userPage.getNumber()+1)
                .pageSize(userPage.getSize())
                .build();
    }
}
