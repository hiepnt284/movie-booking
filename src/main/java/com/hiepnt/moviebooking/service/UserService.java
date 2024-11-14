package com.hiepnt.moviebooking.service;

import com.hiepnt.moviebooking.dto.request.UpdatePasswordDto;
import com.hiepnt.moviebooking.dto.response.BookingItemResponse;
import com.hiepnt.moviebooking.dto.response.UserResponse;
import com.hiepnt.moviebooking.entity.User;
import com.hiepnt.moviebooking.exception.AppException;
import com.hiepnt.moviebooking.exception.ErrorCode;
import com.hiepnt.moviebooking.mapper.UserMapper;
import com.hiepnt.moviebooking.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

        return listBooking.stream().map(booking->
                BookingItemResponse.builder()
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
}
