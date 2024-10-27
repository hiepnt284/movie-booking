package com.hiepnt.moviebooking.controller;

import com.hiepnt.moviebooking.common.ApiResponse;
import com.hiepnt.moviebooking.dto.request.*;
import com.hiepnt.moviebooking.dto.response.LoginRespone;
import com.hiepnt.moviebooking.dto.response.RefreshTokenRequest;
import com.hiepnt.moviebooking.dto.response.RefreshTokenRespone;
import com.hiepnt.moviebooking.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Xác thực", description = "APIs liên quan đến quản lý xác thực")
public class AuthController {

    AuthService authService;

    @Operation(summary = "Đăng ký tài khoản", description = "API để đăng ký tài khoản mới")
    @PostMapping("/register")
    public ApiResponse<?> register(
            @Valid
            @RequestBody RegisterRequest request
    ) {
        return ApiResponse.builder()
                .message(authService.register(request))
                .build();
    }

    @Operation(summary = "Xác nhận đường link xác thực", description = "API để xác nhận đường link xác thực")
    @PostMapping("/verify-link")
    public ApiResponse<Boolean> verifyOtp(
            @RequestBody VerifyRequest request
            ) {
        return ApiResponse.<Boolean>builder()
                .result(authService.verifyLink(request.getParams()))
                .build();
    }
    @Operation(summary = "Xác nhận OTP", description = "API để xác nhận OTP")
    @PostMapping("/verify-otp")
    public ApiResponse<Boolean> verifyOtp(
            @RequestBody() VerifyOtpRequest request
    ) {
        return ApiResponse.<Boolean>builder()
                .result(authService.verifyOtp(request))
                .build();
    }

    @Operation(summary = "Tạo lại OTP", description = "API để tạo lại mã OTP")
    @PostMapping("/regenerate-otp")
    public ApiResponse<String> regenerateOtp (
            @RequestBody RegenerateOtpRequest request
    ) {
        return ApiResponse.<String>builder()
                .message(authService.regenerateOtp(request.getEmail()))
                .build();
    }

    @Operation(summary = "Đăng nhập", description = "API để đăng nhập")
    @PostMapping("/login")
    public ApiResponse<LoginRespone> login(
            @Valid
            @RequestBody LoginRequest request
    ){
        return ApiResponse.<LoginRespone>builder()
                .result(authService.login(request))
                .build();
    }

    @Operation(summary = "Làm mới token", description = "API để làm mới token đăng nhập")
    @PostMapping("/refresh-token")
    public ApiResponse<RefreshTokenRespone> refreshToken(
            @RequestBody RefreshTokenRequest request
    ){
        return ApiResponse.<RefreshTokenRespone>builder()
                .result(authService.refreshToken(request))
                .build();
    }


    @Operation(summary = "Gửi mã OTP đến email để lấy lại mật khẩu", description = "API gửi mã OTP đến email để lấy lại mật khẩu")
    @PostMapping("/forgot-password")
    public ApiResponse<String> forgotPassword(
            @RequestBody RegenerateOtpRequest request) {
        return ApiResponse.<String>builder()
                .message(authService.forgotPassword(request.getEmail()))
                .build();
    }

    @Operation(summary = "Khôi phục lại mật khẩu", description = "Đặt lại mật khẩu cho người dùng")
    @PostMapping("/recover-password")
    public ApiResponse<String> recoverPassword(
            @Valid
            @RequestBody RecoverPasswordDto request) {
        return ApiResponse.<String>builder()
                .message(authService.recoverPassword(request))
                .build();
    }

    @Operation(summary = "Đăng xuất khỏi 1 thiết bị", description = "API để đăng xuất khỏi thiết bị đang sử dụng")
    @PostMapping("/logout-once")
    public ApiResponse<?> logoutOnce(
            @RequestBody LogoutRequest request
    ){
        return ApiResponse.builder()
                .message(authService.logoutOnce(request))
                .build();
    }

    @Operation(summary = "Đăng nhập bằng bên thứ 3", description = "API để đăng nhập qua google")
    @GetMapping("/oauth/google")
    public ApiResponse<LoginRespone> loginWithGoogle(
            @RequestParam("code") String code
    ){
        return ApiResponse.<LoginRespone>builder()
                .result(authService.loginWithGoogle(code))
                .build();
    }
}
