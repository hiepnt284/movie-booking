package com.hiepnt.moviebooking.service;

import com.hiepnt.moviebooking.dto.response.BookingResponse;
import com.hiepnt.moviebooking.entity.enums.EmailType;
import com.hiepnt.moviebooking.util.EncryptionUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailService {
    JavaMailSender emailSender;
    EncryptionUtil encryptionUtil;
    public void sendOtpMail(String email, String otp, EmailType emailType) throws MessagingException {
        MimeMessage mimeMessage = emailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        mimeMessageHelper.setTo(email);

        if (emailType == EmailType.VERIFY_ACCOUNT) {
            mimeMessageHelper.setSubject("Verify your account");

            mimeMessageHelper.setText("""
                <div>
                    <p>Mã OTP của bạn là: %s</p>
                    <p>Sử dụng mã OTP này để xác thực tài khoản của bạn</p>
                </div>
                """.formatted(otp), true);
        } else if(emailType == EmailType.RESET_PASSWORD) {
            mimeMessageHelper.setSubject("Reset your password");

            mimeMessageHelper.setText("""
                <div>
                    <p>Mã OTP của bạn là: %s</p>
                    <p>Sử dụng mã OTP này để khôi phục lại mật khẩu của bạn</p>
                </div>
                """.formatted(otp), true);
        }else {
            mimeMessageHelper.setSubject("Verify your account/Reset your password");

            mimeMessageHelper.setText("""
                <div>
                    <p>Mã OTP của bạn là: %s</p>
                    <p>Sử dụng mã OTP này để xác thực tài khoản của bạn hoặc khôi phục lại mật khẩu của bạn</p>
                </div>
                """.formatted(otp), true);
        }
        emailSender.send(mimeMessage);
    }

    public void sendBookingInfo(BookingResponse bookingResponse) throws MessagingException {
        MimeMessage mimeMessage = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        helper.setTo(bookingResponse.getEmail());
        helper.setSubject("Xác nhận đặt vé thành công");

        String htmlContent = buildBookingEmailContent(bookingResponse);
        helper.setText(htmlContent, true);

        emailSender.send(mimeMessage);
    }

    private String buildBookingEmailContent(BookingResponse bookingResponse) {
        return "<html><body>" +
                "<h3 style='color: green;'>Đặt vé thành công!</h3>" +
                "<p><strong>Khách hàng:</strong> " + bookingResponse.getUserName() + "</p>" +
                "<p><strong>Tổng tiền:</strong> " + bookingResponse.getTotalPrice() + "</p>" +
                "<p><strong>Thời gian thanh toán:</strong> " + bookingResponse.getBookingDate() + "</p>" +
                "<p><strong>Phim:</strong> " + bookingResponse.getMovieTitle() + "</p>" +
                "<p><strong>Thời gian:</strong> " + bookingResponse.getDate() + " " + bookingResponse.getTimeStart() + "</p>" +
                "<p><strong>Rạp:</strong> " + bookingResponse.getTheaterName() + "</p>" +
                "<p><strong>Phòng chiếu:</strong> " + bookingResponse.getRoomName() + "</p>" +
                "<p><strong>Ghế:</strong> " + bookingResponse.getShowSeatNumberList() + "</p>" +
                "<p><strong>Mã vé:</strong> " + bookingResponse.getBookingCode() + "</p>" +
                "<h4>Đồ ăn:</h4>" +
                "<ul>" +
                bookingResponse.getFoodBookingList().stream()
                        .map(item -> "<li>" + item.getQuantity() + " x " + item.getFoodName() + "</li>")
                        .reduce((a, b) -> a + b).orElse("") +
                "</ul>" +
                "<p><img src='" + bookingResponse.getQrCode() + "' alt='QR Code' width='200'/></p>" +
                "</body></html>";
    }

}
