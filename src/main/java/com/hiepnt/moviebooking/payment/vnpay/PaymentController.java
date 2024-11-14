package com.hiepnt.moviebooking.payment.vnpay;

import com.google.zxing.WriterException;
import com.hiepnt.moviebooking.common.ApiResponse;
import com.hiepnt.moviebooking.dto.request.BookingRequest;
import com.hiepnt.moviebooking.entity.ShowSeat;
import com.hiepnt.moviebooking.entity.enums.BookingStatus;
import com.hiepnt.moviebooking.entity.enums.ShowSeatStatus;
import com.hiepnt.moviebooking.exception.AppException;
import com.hiepnt.moviebooking.exception.ErrorCode;
import com.hiepnt.moviebooking.provider.CloudinaryService;
import com.hiepnt.moviebooking.repository.BookingRepository;
import com.hiepnt.moviebooking.repository.ShowSeatRepository;
import com.hiepnt.moviebooking.service.BookingService;
import com.hiepnt.moviebooking.service.EmailService;
import com.hiepnt.moviebooking.util.OtpUtil;
import com.hiepnt.moviebooking.util.QRCodeGenerator;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentController {
    PaymentService paymentService;

    BookingRepository bookingRepository;

    ShowSeatRepository showSeatRepository;

    OtpUtil otpUtil;

    QRCodeGenerator qrCodeGenerator;

    CloudinaryService cloudinaryService;

    EmailService emailService;

    BookingService bookingService;
    @PostMapping("/vn-pay")
    public ApiResponse<VNPayResponse> pay(@RequestBody BookingRequest bookingRequest, HttpServletRequest request) {
        return ApiResponse
                .<VNPayResponse>builder()
                .result(paymentService.createVnPayPayment(bookingRequest, request))
                .build();

    }
    @GetMapping("/vn-pay-callback")
    @Transactional
    public void payCallbackHandler(HttpServletRequest request, HttpServletResponse response) throws IOException, WriterException, MessagingException {
        String status = request.getParameter("vnp_ResponseCode");
        int bookingId = Integer.parseInt(request.getParameter("bookingId"));
        if (status.equals("00")) {
            var booking = bookingRepository.findById(bookingId)
                    .orElseThrow(()-> new AppException(ErrorCode.NOT_EXISTED));
            var list = booking.getSeatBookingList();
            for(var item: list){
                var showSeat = showSeatRepository.findById(item.getShowSeatId())
                        .orElseThrow(()-> new AppException(ErrorCode.NOT_EXISTED));
                showSeat.setShowSeatStatus(ShowSeatStatus.BOOKED);
                showSeatRepository.save(showSeat);
            }
            booking.setBookingCode(otpUtil.generateOtp()+bookingId);

            String qrCodePath = "./qrcodes/" + booking.getBookingCode() + ".png";

            qrCodeGenerator.generateQRCode(booking.getBookingCode());

            File qrFile = new File(qrCodePath);

            String qrCode = cloudinaryService.uploadQr(qrFile);
            booking.setQrCode(qrCode);

            booking.setStatus(BookingStatus.SUCCESS);
            booking.setIsUsed(false);
            bookingRepository.save(booking);

            emailService.sendBookingInfo(bookingService.get(bookingId));

            qrFile.delete();




            response.sendRedirect("http://localhost:3030/payment-success/"+bookingId);
        } else {
            var booking = bookingRepository.findById(bookingId)
                    .orElseThrow(()-> new AppException(ErrorCode.NOT_EXISTED));
            var list = booking.getSeatBookingList();
            for(var item: list){
                var showSeat = showSeatRepository.findById(item.getShowSeatId())
                        .orElseThrow(()-> new AppException(ErrorCode.NOT_EXISTED));
                showSeat.setShowSeatStatus(ShowSeatStatus.AVAILABLE);
                showSeatRepository.save(showSeat);
            }

            bookingRepository.delete(booking);
            response.sendRedirect("http://localhost:3030/payment-fail");
        }
    }
}
