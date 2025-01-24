package com.hiepnt.moviebooking.service;

import com.hiepnt.moviebooking.config.jwt.JwtService;
import com.hiepnt.moviebooking.dto.request.*;
import com.hiepnt.moviebooking.dto.response.LoginRespone;
import com.hiepnt.moviebooking.dto.response.RefreshTokenRequest;
import com.hiepnt.moviebooking.dto.response.RefreshTokenRespone;
import com.hiepnt.moviebooking.entity.Token;
import com.hiepnt.moviebooking.entity.User;
import com.hiepnt.moviebooking.entity.enums.*;
import com.hiepnt.moviebooking.exception.AppException;
import com.hiepnt.moviebooking.exception.ErrorCode;
import com.hiepnt.moviebooking.mapper.UserMapper;
import com.hiepnt.moviebooking.repository.GetGoogleTokenClient;
import com.hiepnt.moviebooking.repository.GetGoogleUserClient;
import com.hiepnt.moviebooking.repository.TokenRepository;
import com.hiepnt.moviebooking.repository.UserRepository;
import com.hiepnt.moviebooking.util.EncryptionUtil;
import com.hiepnt.moviebooking.util.OtpUtil;
import jakarta.mail.MessagingException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthService {
    UserRepository userRepository;
    EmailService emailService;
    OtpUtil otpUtil;
    EncryptionUtil encryptionUtil;
    JwtService jwtService;
    TokenRepository tokenRepository;
    PasswordEncoder passwordEncoder;
    UserMapper userMapper;

    GetGoogleTokenClient getGoogleTokenClient;

    GetGoogleUserClient getGoogleUserClient;


    @NonFinal
    @Value("${google.client-id}")
    protected String CLIENT_ID;

    @NonFinal
    @Value("${google.client-secret}")
    protected String CLIENT_SECRET;

    @NonFinal
    @Value("${google.redirect-uri}")
    protected String REDIRECT_URI;

    @NonFinal
    protected final String GRANT_TYPE = "authorization_code";

    public String register(RegisterRequest request) {
        var optionalUser = userRepository.findByEmail(request.getEmail());
        if(optionalUser.isPresent()){
            User existingUser = optionalUser.get();
            if (existingUser.getIsActive() != Status.PENDING) {
                throw new AppException(ErrorCode.EXISTED);
            }
            String otp = otpUtil.generateOtp();
            try {
                emailService.sendOtpMail(request.getEmail(), otp, EmailType.VERIFY_ACCOUNT);
            } catch (MessagingException e) {
                throw new AppException(ErrorCode.FAIL_TO_SEND_OTP);
            }
            existingUser.setOtp(otp);
            existingUser.setOtpGeneratedTime(LocalDateTime.now());
            userRepository.save(existingUser);
        }else{
            String otp = otpUtil.generateOtp();
            try {
                emailService.sendOtpMail(request.getEmail(), otp, EmailType.VERIFY_ACCOUNT);
            } catch (MessagingException e) {
                throw new AppException(ErrorCode.FAIL_TO_SEND_OTP);
            }
            User user = User.builder()
                    .fullName(request.getFullName())
                    .email(request.getEmail())
                    .phoneNumber(request.getPhoneNumber())
                    .gender(request.getGender())
                    .dob(request.getDob())
                    .isActive(Status.PENDING)
                    .role(Role.USER)
                    .password(passwordEncoder.encode(request.getPassword()))
                    .otp(otp)
                    .otpGeneratedTime(LocalDateTime.now())
                    .build();
            userRepository.save(user);
        }
        return "Success";
    }

    public boolean verifyLink(String params) {
        String decodedParams = encryptionUtil.decodeBase64(params);
        String [] splitParams = decodedParams.split("&");
        String email = splitParams[0].split("=")[1];
        String otp = splitParams[1].split("=")[1];

        var user = userRepository.findByEmail(email)
                .orElseThrow(()-> new AppException(ErrorCode.NOT_EXISTED));

        if (user.getIsActive() == Status.BANNED)
            throw new AppException(ErrorCode.FORBIDDEN_ACCOUNT);

        if(user.getOtp().equals(otp) && Duration.between(user.getOtpGeneratedTime(),
                LocalDateTime.now()).getSeconds() < (5 * 60)) {
            if(user.getIsActive() == Status.PENDING) {
                user.setIsActive(Status.ACTIVE);
                userRepository.save(user);
            }
            return true;
        }
        return false;
    }

//    public boolean verifyOtp(VerifyOtpRequest request) {
//        var user = userRepository.findByEmail(request.getEmail())
//                .orElseThrow(()-> new AppException(ErrorCode.NOT_EXISTED));
//
//        if (user.getIsActive() == Status.BANNED)
//            throw new AppException(ErrorCode.FORBIDDEN_ACCOUNT);
//
//        if(user.getOtp().equals(request.getOtp()) && Duration.between(user.getOtpGeneratedTime(),
//                LocalDateTime.now()).getSeconds() < (5 * 60)) {
//            if(user.getIsActive() == Status.PENDING) {
//                user.setIsActive(Status.ACTIVE);
//                userRepository.save(user);
//            }
//            return true;
//        }
//        return false;
//    }

    public String regenerateOtp(String email) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(()-> new AppException(ErrorCode.NOT_EXISTED));
        String otp = otpUtil.generateOtp();
        try {
            emailService.sendOtpMail(email, otp, EmailType.REGENERATE);
        } catch (MessagingException e) {
            throw new AppException(ErrorCode.FAIL_TO_SEND_OTP);
        }
        user.setOtp(otp);
        user.setOtpGeneratedTime(LocalDateTime.now());
        userRepository.save(user);
        return "Email sent... Please verify account within 5 minutes";
    }


    public LoginRespone login(LoginRequest request){

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));

        if (user.getIsActive() == Status.PENDING){
            throw new AppException(ErrorCode.UNVERIFIED_ACCOUNT);
        }

        if (user.getIsActive() == Status.BANNED){
            throw new AppException(ErrorCode.FORBIDDEN_ACCOUNT);
        }

        boolean authenticated = passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        );

        if (!authenticated) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        var accessToken = jwtService.generateAccessToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        var storedToken = Token.builder()
                .token(refreshToken)
                .tokenType(TokenType.BEARER)
                .user(user)
                .build();
        tokenRepository.save(storedToken);

        var userRes = userMapper.toUserResponse(user);
        if(user.getTheater()!=null){
            userRes.setTheaterId(user.getTheater().getId());
        }else{
            userRes.setTheaterId(null);
        }

        userRes.setIsActive(user.getIsActive());

        return LoginRespone.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userResponse(userRes)
                .build();
    }

    public RefreshTokenRespone refreshToken(RefreshTokenRequest request){
        if(!jwtService.verifyToken(request.getRefreshToken())){
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }
        var storedToken = tokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(()->new AppException(ErrorCode.INVALID_TOKEN));
        var user = storedToken.getUser();
        tokenRepository.delete(storedToken);

        var accessToken = jwtService.generateAccessToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        var newRefreshToken = Token.builder()
                .token(refreshToken)
                .user(user)
                .build();

        tokenRepository.save(newRefreshToken);

        return RefreshTokenRespone.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public String recoverPassword(RecoverPasswordDto request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));

        if (!user.getOtp().equals(request.getOtp())||!(Duration.between(user.getOtpGeneratedTime(),
                LocalDateTime.now()).getSeconds() < (5 * 60))) {
            throw new AppException(ErrorCode.INVALID_INPUT_DATA);
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new AppException(ErrorCode.INVALID_INPUT_DATA);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return "Your password has been changed";
    }

    public String forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));
        String otp = otpUtil.generateOtp();
        try {
            emailService.sendOtpMail(email, otp, EmailType.RESET_PASSWORD);
        } catch (MessagingException e) {
            throw new AppException(ErrorCode.FAIL_TO_SEND_OTP);
        }
        user.setOtp(otp);
        user.setOtpGeneratedTime(LocalDateTime.now());
        userRepository.save(user);
        return "Email sent... Please verify account within 5 minutes";
    }

    public String logoutOnce(LogoutRequest request){
        if(!jwtService.verifyToken(request.getRefreshToken())){
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }
        var storedToken = tokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(()->new AppException(ErrorCode.INVALID_TOKEN));
        tokenRepository.delete(storedToken);
        return "Success";
    }

    public LoginRespone loginWithGoogle(String code) {
        var tokenResponse = getGoogleTokenClient.exchangeToken(
                ExchangeTokenRequest.builder()
                        .code(code)
                        .clientId(CLIENT_ID)
                        .clientSecret(CLIENT_SECRET)
                        .redirectUri(REDIRECT_URI)
                        .grantType(GRANT_TYPE)
                        .build()
        );

        // Lấy accessToken
        String googleAccessToken = tokenResponse.getAccessToken();

// Thiết lập Authorization header
        String authorizationHeader = "Bearer " + googleAccessToken;

// Xác định các trường cần lấy
        String personFields = "names,emailAddresses,phoneNumbers,genders,birthdays,photos";

// Gọi API để lấy thông tin người dùng
        var userResponse = getGoogleUserClient.getUserInfo(authorizationHeader, personFields);

        String emailUser = userResponse.getEmailAddresses().get(0).getValue();
        var birthday = userResponse.getBirthdays().get(0);
        var user = userRepository.findByEmail(emailUser).orElse(
                User.builder()
                        .isGgAccount(true)
                        .email(emailUser)
                        .fullName(userResponse.getNames().get(0).getDisplayName())
                        .role(Role.USER)
                        .avt(userResponse.getPhotos().get(0).getUrl())
                        .phoneNumber(userResponse.getPhoneNumbers().get(0).getValue().replaceAll("\\s+", ""))
                        .gender(Gender.valueOf(userResponse.getGenders().get(0).getValue().toUpperCase()))
                        .dob(LocalDate.of(
                                birthday.getDate().getYear(),
                                birthday.getDate().getMonth(),
                                birthday.getDate().getDay()
                        ))
                        .isActive(Status.ACTIVE)
                        .build()
        );
        var storedUser = userRepository.save(user);
        var accessToken = jwtService.generateAccessToken(storedUser);
        var refreshToken = jwtService.generateRefreshToken(storedUser);

        Token token = Token.builder()
                .token(refreshToken)
                .tokenType(TokenType.BEARER)
                .user(user)
                .build();

        tokenRepository.save(token);

        return LoginRespone.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userResponse(userMapper.toUserResponse(user))
                .build();
    }

    public boolean verifyOtp(VerifyOtpRequest request) {
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(()-> new AppException(ErrorCode.NOT_EXISTED));

        if (user.getIsActive() == Status.BANNED)
            throw new AppException(ErrorCode.FORBIDDEN_ACCOUNT);

        if(user.getOtp().equals(request.getOtp()) && Duration.between(user.getOtpGeneratedTime(),
                LocalDateTime.now()).getSeconds() < (5 * 60)) {
            if(user.getIsActive() == Status.PENDING) {
                user.setIsActive(Status.ACTIVE);
                userRepository.save(user);
            }
            return true;
        }else{
            throw new AppException(ErrorCode.INVALID_OTP);
        }
    }
}
