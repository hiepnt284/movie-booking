package com.hiepnt.moviebooking.service;

import com.hiepnt.moviebooking.dto.response.BookingResponse;
import com.hiepnt.moviebooking.dto.response.FoodBookingResponse;
import com.hiepnt.moviebooking.entity.Booking;
import com.hiepnt.moviebooking.entity.enums.BookingStatus;
import com.hiepnt.moviebooking.entity.enums.ShowSeatStatus;
import com.hiepnt.moviebooking.exception.AppException;
import com.hiepnt.moviebooking.exception.ErrorCode;
import com.hiepnt.moviebooking.repository.BookingRepository;
import com.hiepnt.moviebooking.repository.ShowSeatRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookingService {
    BookingRepository bookingRepository;
    ShowSeatRepository showSeatRepository;
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

    public String verifyQRCode(String bookingCode){
        Booking booking = bookingRepository.findByBookingCode(bookingCode);
        if (booking != null && !booking.getIsUsed()) {
            // Cập nhật trạng thái vé là đã được sử dụng
            booking.setIsUsed(true);
            bookingRepository.save(booking);
            return "Vé hợp lệ";
        } else {
            return "Vé không hợp lệ";
        }

    }
}
