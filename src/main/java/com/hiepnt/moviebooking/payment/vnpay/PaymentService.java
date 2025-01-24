package com.hiepnt.moviebooking.payment.vnpay;

import com.hiepnt.moviebooking.config.payment.VNPAYConfig;
import com.hiepnt.moviebooking.dto.request.BookingRequest;
import com.hiepnt.moviebooking.dto.request.FoodBookingRequest;
import com.hiepnt.moviebooking.entity.*;
import com.hiepnt.moviebooking.entity.enums.BookingStatus;
import com.hiepnt.moviebooking.entity.enums.ShowSeatStatus;
import com.hiepnt.moviebooking.exception.AppException;
import com.hiepnt.moviebooking.exception.ErrorCode;
import com.hiepnt.moviebooking.repository.*;
import com.hiepnt.moviebooking.util.OtpUtil;
import com.hiepnt.moviebooking.util.VNPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentService {
    VNPAYConfig vnPayConfig;

    BookingRepository bookingRepository;

    ShowSeatRepository showSeatRepository;

    SeatBookingRepository seatBookingRepository;

    FoodBookingRepository foodBookingRepository;

    FoodRepository foodRepository;

    ShowTimeRepository showTimeRepository;



    @Transactional
    public VNPayResponse createVnPayPayment(BookingRequest bookingRequest, HttpServletRequest request) {

        List<Integer> listShowSeatId = bookingRequest.getListShowSeatId();

        for (int showSeatId : listShowSeatId) {
            var showSeat = showSeatRepository.findById(showSeatId)
                    .orElseThrow(()-> new AppException(ErrorCode.NOT_EXISTED));
            if (!showSeat.getShowSeatStatus().equals(ShowSeatStatus.AVAILABLE))
                throw new AppException(ErrorCode.UNAVAILABLE_SEAT);
        }


        long amount = (int)(bookingRequest.getTotalPrice()) * 100L;
        int userId = bookingRequest.getUserId();
        LocalDateTime bookingDate = LocalDateTime.now();
        int showtimeId = bookingRequest.getShowtimeId();
        var showtime = showTimeRepository.findById(showtimeId)
                .orElseThrow(()-> new AppException(ErrorCode.NOT_EXISTED));
        var theaterId = showtime.getRoom().getTheater().getId();
        var movieId  = showtime.getMovie().getId();
        String listShowSeatNumber = bookingRequest.getListShowSeatNumber();
        List<FoodBookingRequest> foodBookingRequestList = bookingRequest.getFoodBookingRequestList();


        Booking booking = Booking.builder()
                .theaterId(theaterId)
                .movieId(movieId)
                .totalPrice(bookingRequest.getTotalPrice())
                .user(User.builder().id(userId).build())
                .bookingDate(bookingDate)
                .showtime(Showtime.builder().id(showtimeId).build())
                .showSeatNumberList(listShowSeatNumber)
                .status(BookingStatus.PENDING)
                .build();

        var saveBooking = bookingRepository.save(booking);
        int bookingId = saveBooking.getId();


        for (int showSeatId : listShowSeatId) {
            var showSeat = showSeatRepository.findById(showSeatId)
                    .orElseThrow(()-> new AppException(ErrorCode.NOT_EXISTED));
            showSeat.setShowSeatStatus(ShowSeatStatus.SELECTED);
            showSeatRepository.save(showSeat);
            var seatBooking = SeatBooking.builder().showSeatId(showSeatId).booking(saveBooking).build();
            seatBookingRepository.save(seatBooking);
        }

        for(FoodBookingRequest foodBookingRequest: foodBookingRequestList){
            var food = foodRepository.findById(foodBookingRequest.getFoodId())
                    .orElseThrow(()-> new AppException(ErrorCode.NOT_EXISTED));
            var foodBooking = FoodBooking.builder()
                    .name(food.getName())
                    .price(food.getPrice())
                    .quantity(foodBookingRequest.getQuantity())
                    .booking(saveBooking)
                    .build();
            foodBookingRepository.save(foodBooking);
        }


        String vnp_ReturnUrl = "http://localhost:8080/payment/vn-pay-callback";
//        String bankCode = request.getParameter("bankCode");
        Map<String, String> vnpParamsMap = vnPayConfig.getVNPayConfig();
        vnpParamsMap.put("vnp_Amount", String.valueOf(amount));
//        if (bankCode != null && !bankCode.isEmpty()) {
//            vnpParamsMap.put("vnp_BankCode", bankCode);
//        }
        vnpParamsMap.put("vnp_IpAddr", VNPayUtil.getIpAddress(request));
        vnpParamsMap.put("vnp_ReturnUrl", vnp_ReturnUrl+"?bookingId="+bookingId);
        //build query url
        String queryUrl = VNPayUtil.getPaymentURL(vnpParamsMap, true);
        String hashData = VNPayUtil.getPaymentURL(vnpParamsMap, false);
        String vnpSecureHash = VNPayUtil.hmacSHA512(vnPayConfig.getSecretKey(), hashData);
        queryUrl += "&vnp_SecureHash=" + vnpSecureHash;
        String paymentUrl = vnPayConfig.getVnp_PayUrl() + "?" + queryUrl;
        return VNPayResponse.builder()
                .code("ok")
                .message("success")
                .paymentUrl(paymentUrl).build();
    }
}
