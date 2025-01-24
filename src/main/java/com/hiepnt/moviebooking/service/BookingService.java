package com.hiepnt.moviebooking.service;

import com.google.zxing.WriterException;
import com.hiepnt.moviebooking.dto.request.BookingRequest;
import com.hiepnt.moviebooking.dto.request.FoodBookingRequest;
import com.hiepnt.moviebooking.dto.response.BookingResponse;
import com.hiepnt.moviebooking.dto.response.FoodBookingResponse;
import com.hiepnt.moviebooking.entity.*;
import com.hiepnt.moviebooking.entity.enums.BookingStatus;
import com.hiepnt.moviebooking.entity.enums.ShowSeatStatus;
import com.hiepnt.moviebooking.exception.AppException;
import com.hiepnt.moviebooking.exception.ErrorCode;
import com.hiepnt.moviebooking.payment.vnpay.VNPayResponse;
import com.hiepnt.moviebooking.repository.*;
import com.hiepnt.moviebooking.util.OtpUtil;
import com.hiepnt.moviebooking.util.QRCodeGenerator;
import com.hiepnt.moviebooking.util.VNPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookingService {
    BookingRepository bookingRepository;
    ShowSeatRepository showSeatRepository;
    ShowTimeRepository showTimeRepository;
    SeatBookingRepository seatBookingRepository;
    FoodRepository foodRepository;
    FoodBookingRepository foodBookingRepository;
    OtpUtil otpUtil;

    QRCodeGenerator qrCodeGenerator;
    @Scheduled(fixedRate = 10000)
    @Transactional
    public void cancelExpiredBooking(){
        var bookingList = bookingRepository.findByStatus(BookingStatus.PENDING);
        for(var booking: bookingList){
            if(LocalDateTime.now().isAfter(booking.getBookingDate().plusMinutes(5))){
                var seatList = booking.getSeatBookingList();
                for(var item: seatList){
                    var showSeat = showSeatRepository.findById(item.getShowSeatId())
                            .orElseThrow(()-> new AppException(ErrorCode.NOT_EXISTED));
                    showSeat.setShowSeatStatus(ShowSeatStatus.AVAILABLE);
                    showSeatRepository.save(showSeat);
                }
                bookingRepository.delete(booking);
            };
        }
    }

    @Transactional
    public BookingResponse get(int id){
        var booking = bookingRepository.findById(id)
                .orElseThrow(()-> new AppException(ErrorCode.NOT_EXISTED));
        var listFood = booking.getFoodBookingList().stream()
                .map(item-> FoodBookingResponse
                        .builder()
                        .foodName(item.getName())
                        .quantity(item.getQuantity())
                        .build()).toList();

        return BookingResponse
                .builder()
                .id(booking.getId())
                .totalPrice(booking.getTotalPrice())
                .userName(booking.getUser().getFullName())
                .email(booking.getUser().getEmail())
                .bookingDate(booking.getBookingDate())
                .movieTitle(booking.getShowtime().getMovie().getTitle())
                .date(booking.getShowtime().getDate())
                .timeStart(booking.getShowtime().getTimeStart())
                .theaterName(booking.getShowtime().getRoom().getTheater().getName())
                .roomName(booking.getShowtime().getRoom().getName())
                .showSeatNumberList(booking.getShowSeatNumberList())
                .foodBookingList(listFood)
                .bookingCode(booking.getBookingCode())
                .qrCode(booking.getQrCode())
                .isUsed(booking.getIsUsed())
                .build();
    }
    @Transactional
    public BookingResponse verifyQRCode(String bookingCode){
        Booking booking = bookingRepository.findByBookingCode(bookingCode);
        if (booking != null && !booking.getIsUsed()) {
            // Cập nhật trạng thái vé là đã được sử dụng
            booking.setIsUsed(true);
            bookingRepository.save(booking);
            var listFood = booking.getFoodBookingList().stream()
                    .map(item-> FoodBookingResponse
                            .builder()
                            .foodName(item.getName())
                            .quantity(item.getQuantity())
                            .build()).toList();

            return BookingResponse
                    .builder()
                    .id(booking.getId())
                    .totalPrice(booking.getTotalPrice())
                    .userName(booking.getUser().getFullName())
                    .email(booking.getUser().getEmail())
                    .bookingDate(booking.getBookingDate())
                    .movieTitle(booking.getShowtime().getMovie().getTitle())
                    .date(booking.getShowtime().getDate())
                    .timeStart(booking.getShowtime().getTimeStart())
                    .theaterName(booking.getShowtime().getRoom().getTheater().getName())
                    .roomName(booking.getShowtime().getRoom().getName())
                    .showSeatNumberList(booking.getShowSeatNumberList())
                    .foodBookingList(listFood)
                    .bookingCode(booking.getBookingCode())
                    .qrCode(booking.getQrCode())
                    .isUsed(booking.getIsUsed())
                    .build();
        } else {
            throw new AppException(ErrorCode.NOT_EXISTED);
        }

    }

    @Transactional
    public BookingResponse bookingOff(BookingRequest bookingRequest) {

        List<Integer> listShowSeatId = bookingRequest.getListShowSeatId();

        // Lấy toàn bộ danh sách ghế từ database
        var showSeats = showSeatRepository.findAllById(listShowSeatId);
        if (showSeats.size() != listShowSeatId.size()) {
            throw new AppException(ErrorCode.NOT_EXISTED);
        }

        // Kiểm tra trạng thái của ghế
        showSeats.forEach(seat -> {
            if (!seat.getShowSeatStatus().equals(ShowSeatStatus.AVAILABLE)) {
                throw new AppException(ErrorCode.UNAVAILABLE_SEAT);
            }
        });

        // Xử lý thông tin đặt vé
        int userId = bookingRequest.getUserId();
        long totalPrice = (long) bookingRequest.getTotalPrice();
        LocalDateTime bookingDate = LocalDateTime.now();

        var showtime = showTimeRepository.findById(bookingRequest.getShowtimeId())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));

        // Lấy thông tin liên quan
        var theaterId = showtime.getRoom().getTheater().getId();
        var movieId = showtime.getMovie().getId();
        String listShowSeatNumber = bookingRequest.getListShowSeatNumber();
        List<FoodBookingRequest> foodBookingRequestList = bookingRequest.getFoodBookingRequestList();

        // Tạo Booking entity
        Booking booking = Booking.builder()
                .theaterId(theaterId)
                .movieId(movieId)
                .totalPrice(totalPrice)
                .user(User.builder().id(userId).build())
                .bookingDate(bookingDate)
                .showtime(showtime)
                .showSeatNumberList(listShowSeatNumber)
                .status(BookingStatus.SUCCESS)
                .isUsed(true)
                .build();

        // Lưu booking và cập nhật booking code
        var savedBooking = bookingRepository.save(booking);
        savedBooking.setBookingCode(otpUtil.generateOtp() + savedBooking.getId());
        bookingRepository.save(savedBooking);

        // Cập nhật trạng thái ghế và tạo danh sách SeatBooking
        showSeats.forEach(seat -> {
            seat.setShowSeatStatus(ShowSeatStatus.BOOKED);
        });
        showSeatRepository.saveAll(showSeats);

        List<SeatBooking> seatBookings = listShowSeatId.stream()
                .map(seatId -> SeatBooking.builder()
                        .showSeatId(seatId)
                        .booking(savedBooking)
                        .build())
                .toList();
        seatBookingRepository.saveAll(seatBookings);

        // Lưu thông tin đặt đồ ăn
        List<FoodBooking> foodBookings = foodBookingRequestList.stream()
                .map(foodBookingRequest -> {
                    var food = foodRepository.findById(foodBookingRequest.getFoodId())
                            .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));
                    return FoodBooking.builder()
                            .name(food.getName())
                            .price(food.getPrice())
                            .quantity(foodBookingRequest.getQuantity())
                            .booking(savedBooking)
                            .build();
                }).toList();
        foodBookingRepository.saveAll(foodBookings);

        // Chuẩn bị dữ liệu phản hồi
        var foodBookingResponses = foodBookings.stream()
                .map(item -> FoodBookingResponse.builder()
                        .foodName(item.getName())
                        .quantity(item.getQuantity())
                        .build())
                .toList();

        return BookingResponse.builder()
                .id(savedBooking.getId())
                .totalPrice(savedBooking.getTotalPrice())
                .userName(savedBooking.getUser().getFullName())
                .email(savedBooking.getUser().getEmail())
                .bookingDate(savedBooking.getBookingDate())
                .movieTitle(savedBooking.getShowtime().getMovie().getTitle())
                .date(savedBooking.getShowtime().getDate())
                .timeStart(savedBooking.getShowtime().getTimeStart())
                .theaterName(savedBooking.getShowtime().getRoom().getTheater().getName())
                .roomName(savedBooking.getShowtime().getRoom().getName())
                .showSeatNumberList(savedBooking.getShowSeatNumberList())
                .foodBookingList(foodBookingResponses)
                .bookingCode(savedBooking.getBookingCode())
                .qrCode(savedBooking.getQrCode())
                .isUsed(savedBooking.getIsUsed())
                .build();
    }


}
