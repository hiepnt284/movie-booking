package com.hiepnt.moviebooking.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Uncategorized error", HttpStatus.BAD_REQUEST),

    EXISTED(1101, "Resource existed", HttpStatus.BAD_REQUEST),
    NOT_EXISTED(1102, "Resource not existed", HttpStatus.NOT_FOUND),

    INVALID_INPUT_DATA(1002, "Invalid input data", HttpStatus.BAD_REQUEST),
    NOT_IMG(1111,"File is not image",HttpStatus.BAD_REQUEST),
    INVALID_TOKEN(1003,"Token invalid", HttpStatus.UNAUTHORIZED),
    FILE_TOO_LARGE(1004, "The file size exceeds the maximum limit", HttpStatus.BAD_REQUEST),

    UNAUTHENTICATED(1201, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1202, "You do not have permission", HttpStatus.FORBIDDEN),
    UNVERIFIED_ACCOUNT(1203, "Your account is not verified", HttpStatus.BAD_REQUEST),
    FORBIDDEN_ACCOUNT(1204, "Your account has been banned", HttpStatus.BAD_REQUEST),
    FAIL_TO_SEND_OTP(1301, "Unable to send OTP, please try again", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_OTP(1302, "Invalid otp", HttpStatus.BAD_REQUEST),

    UNAVAILABLE_SEAT(1400, "unavailable seat", HttpStatus.BAD_REQUEST);

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    int code;
    String message;
    HttpStatusCode statusCode;
}
