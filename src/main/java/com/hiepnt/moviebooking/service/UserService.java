package com.hiepnt.moviebooking.service;

import com.hiepnt.moviebooking.common.PageResponse;
import com.hiepnt.moviebooking.dto.request.UpdatePasswordDto;
import com.hiepnt.moviebooking.dto.response.BookingItemResponse;
import com.hiepnt.moviebooking.dto.response.UserResponse;
import com.hiepnt.moviebooking.entity.User;
import com.hiepnt.moviebooking.entity.enums.Status;
import com.hiepnt.moviebooking.exception.AppException;
import com.hiepnt.moviebooking.exception.ErrorCode;
import com.hiepnt.moviebooking.mapper.UserMapper;
import com.hiepnt.moviebooking.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserMapper userMapper;
    UserRepository userRepository;

    PasswordEncoder passwordEncoder;
    public UserResponse getInfo(){
        var context = SecurityContextHolder.getContext();
        int userId = Integer.parseInt(context.getAuthentication().getName());

        return userMapper.toUserResponse(userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED)));
    }

    public Void changePassword(UpdatePasswordDto request) {
        var context = SecurityContextHolder.getContext();
        int userId = Integer.parseInt(context.getAuthentication().getName());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.INVALID_INPUT_DATA);
        }
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new AppException(ErrorCode.INVALID_INPUT_DATA);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        return null;
    }
    @Transactional
    public List<BookingItemResponse> getBookingHistory() {
        var context = SecurityContextHolder.getContext();
        int userId = Integer.parseInt(context.getAuthentication().getName());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));
        var listBooking = user.getBookings();

        return listBooking.stream()
                .sorted((b1, b2) -> b2.getBookingDate().compareTo(b1.getBookingDate())) // Sắp xếp giảm dần theo bookingDate
                .limit(5) // Lấy 5 giao dịch mới nhất
                .map(booking -> BookingItemResponse.builder()
                        .id(booking.getId())
                        .totalPrice(booking.getTotalPrice())
                        .bookingDate(booking.getBookingDate())
                        .movieTitle(booking.getShowtime().getMovie().getTitle())
                        .theaterName(booking.getShowtime().getRoom().getTheater().getName())
                        .roomName(booking.getShowtime().getRoom().getName())
                        .showSeatNumberList(booking.getShowSeatNumberList())
                        .bookingCode(booking.getBookingCode())
                        .build()
                ).toList();
    }



    public PageResponse<UserResponse> getAll(
            int page,
            int pageSize,
            String sortBy,
            String direction,
            String keyword
    ){
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.fromString(direction), sortBy));
        Page<User> userPage = userRepository.findByEmail(
                (keyword == null || keyword.trim().isEmpty()) ? null : keyword,
                "admin@gmail.com",
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

    public String changeStatus(int id){
        var user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));
        user.setIsActive(user.getIsActive()== Status.ACTIVE?Status.BANNED:Status.ACTIVE);
        userRepository.save(user);
        return "Success";
    }
}
