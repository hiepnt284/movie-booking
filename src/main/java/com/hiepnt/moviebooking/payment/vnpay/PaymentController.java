package com.hiepnt.moviebooking.payment.vnpay;

import com.hiepnt.moviebooking.common.ApiResponse;
import com.hiepnt.moviebooking.dto.request.BookingRequest;
import com.hiepnt.moviebooking.entity.ShowSeat;
import com.hiepnt.moviebooking.exception.AppException;
import com.hiepnt.moviebooking.exception.ErrorCode;
import com.hiepnt.moviebooking.repository.BookingRepository;
import com.hiepnt.moviebooking.repository.ShowSeatRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentController {
    PaymentService paymentService;

    BookingRepository bookingRepository;

    ShowSeatRepository showSeatRepository;
    @PostMapping("/vn-pay")
    public ApiResponse<VNPayResponse> pay(@RequestBody BookingRequest brequest, HttpServletRequest request) {
        return ApiResponse
                .<VNPayResponse>builder()
                .result(paymentService.createVnPayPayment(brequest, request))
                .build();

    }
    @GetMapping("/vn-pay-callback")
    public void payCallbackHandler(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String status = request.getParameter("vnp_ResponseCode");
        if (status.equals("00")) {
            response.sendRedirect("http://localhost:3030/payment-success");
        } else {
            response.sendRedirect("http://localhost:3030/payment-fail");
        }
    }
}
