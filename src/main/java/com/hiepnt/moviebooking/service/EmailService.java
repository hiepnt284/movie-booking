package com.hiepnt.moviebooking.service;

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

}
