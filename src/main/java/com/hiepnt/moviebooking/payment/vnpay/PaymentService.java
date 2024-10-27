package com.hiepnt.moviebooking.payment.vnpay;

import com.hiepnt.moviebooking.config.payment.VNPAYConfig;
import com.hiepnt.moviebooking.dto.request.BookingRequest;
import com.hiepnt.moviebooking.entity.Booking;
import com.hiepnt.moviebooking.entity.Showtime;
import com.hiepnt.moviebooking.entity.User;
import com.hiepnt.moviebooking.entity.enums.BookingStatus;
import com.hiepnt.moviebooking.entity.enums.ShowSeatStatus;
import com.hiepnt.moviebooking.repository.BookingRepository;
import com.hiepnt.moviebooking.repository.ShowSeatRepository;
import com.hiepnt.moviebooking.util.VNPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentService {
    VNPAYConfig vnPayConfig;

    BookingRepository bookingRepository;

    ShowSeatRepository showSeatRepository;


    public VNPayResponse createVnPayPayment(BookingRequest brequest, HttpServletRequest request) {
        String vnp_ReturnUrl = "http://localhost:8080/payment/vn-pay-callback";
        long amount = (int)(brequest.getTotalPrice()) * 100L;
        int userId = brequest.getUserId();
        LocalDateTime bookingDate = brequest.getBookingDate();
        int showtimeId = brequest.getShowtimeId();
        String listShowSeatNumber = brequest.getListShowSeatNumber();
        var listShowSeatId = brequest.getListShowSeatId();

        Booking booking = Booking.builder()
                .totalPrice(brequest.getTotalPrice())
                .user(User.builder().id(userId).build())
                .bookingDate(bookingDate)
                .showtime(Showtime.builder().id(showtimeId).build())
                .showSeatNumberList(listShowSeatNumber)
                .status(BookingStatus.PENDING)
                .build();

        int bookingId = bookingRepository.save(booking).getId();

        for (int showSeatId : listShowSeatId) {
            var showSeat = showSeatRepository.findById(showSeatId);
            if (showSeat.isPresent()) {
                showSeat.get().setShowSeatStatus(ShowSeatStatus.BOOKED);
                showSeatRepository.save(showSeat.get());
            } else {
                // Xử lý trường hợp showSeat không tồn tại
                throw new RuntimeException("ShowSeat with id " + showSeatId + " not found.");
            }
        }



        String bankCode = request.getParameter("bankCode");
        Map<String, String> vnpParamsMap = vnPayConfig.getVNPayConfig();
        vnpParamsMap.put("vnp_Amount", String.valueOf(amount));
        if (bankCode != null && !bankCode.isEmpty()) {
            vnpParamsMap.put("vnp_BankCode", bankCode);
        }
        vnpParamsMap.put("vnp_IpAddr", VNPayUtil.getIpAddress(request));
        vnpParamsMap.put("vnp_ReturnUrl", vnp_ReturnUrl);
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
