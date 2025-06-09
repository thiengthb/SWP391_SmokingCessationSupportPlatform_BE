package com.swpteam.smokingcessation.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    INVALID_KEY(999, "Uncategorized error", HttpStatus.BAD_REQUEST),
    INVALID_ENUM(1009, "Message type cannot be empty", HttpStatus.INTERNAL_SERVER_ERROR),
    //common
    CONTENT_REQUIRED(1001, "Message content cannot be empty", HttpStatus.BAD_REQUEST),
    MESSAGE_TYPE_REQUIRED(1006, "Message type cannot be empty", HttpStatus.BAD_REQUEST),
    EMAIL_REQUIRED(1000, "Email field cannot be empty", HttpStatus.BAD_REQUEST),
    ACCOUNT_REQUIRED(1009, "Account field cannot be empty", HttpStatus.BAD_REQUEST),
    //validation
    INVALID_ACCOUNT_ID(1007, "Account ID does not exist", HttpStatus.BAD_REQUEST),
    EMAIL_INVALID(1002, "Invalid Email form", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1003, "Password must be at least {min} characters", HttpStatus.BAD_REQUEST),
    BLANK_INVALID(1004, "Field must not be blank", HttpStatus.BAD_REQUEST),
    PHONE_NUMBER_INVALID(1005, "Phone number must be 10 digits and consists of numbers only", HttpStatus.BAD_REQUEST),
    //authentication
    ACCOUNT_NOT_EXISTED(2000, "Account does not exist in the system", HttpStatus.NOT_FOUND),
    INVALID_SIGNATURE(2003, "Invalid JWT signature", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED(2004, "Token is expired", HttpStatus.UNAUTHORIZED),
    WRONG_PASSWORD(2005, "Wrong password for email", HttpStatus.BAD_REQUEST),
    //account
    ACCOUNT_EXISTED(3000, "Account already exist in the system", HttpStatus.BAD_REQUEST),
    ACCOUNT_DELETED(3001, "Account has been deleted", HttpStatus.BAD_REQUEST),
    //health
    HEALTH_RECORD_NOT_FOUND(4000, "Health record does not exist", HttpStatus.NOT_FOUND),
    CIGARETTES_PER_DAY_INVALID(4002, "Cigarettes per day must be non-negative", HttpStatus.BAD_REQUEST),
    CIGARETTES_PER_PACK_INVALID(4003, "Cigarettes per pack must be non-negative", HttpStatus.BAD_REQUEST),
    FND_LEVEL_INVALID_MIN(4004, "FND level must be >= 0", HttpStatus.BAD_REQUEST),
    FND_LEVEL_INVALID_MAX(4005, "FND level must be <= 10", HttpStatus.BAD_REQUEST),
    PACK_PRICE_INVALID(4006, "Pack price must be non-negative", HttpStatus.BAD_REQUEST),
    REASON_TO_QUIT_REQUIRED(4007, "Reason to quit is required", HttpStatus.BAD_REQUEST),
    REASON_TO_QUIT_TOO_LONG(4008, "Reason to quit must not exceed 255 characters", HttpStatus.BAD_REQUEST),
    SMOKE_YEAR_INVALID(4009, "Smoke year must be non-negative", HttpStatus.BAD_REQUEST),
    ACCESS_DENIED(4001, "Access denied", HttpStatus.FORBIDDEN),
    PACK_PRICE_TOO_HIGH(4010, "Pack price must not exceed 500.0", HttpStatus.BAD_REQUEST),
    //record
    RECORD_NOT_FOUND(5000, "Record does not exist", HttpStatus.NOT_FOUND),
    RECORD_ACCOUNT_ID_REQUIRED(5001, "Account ID is required", HttpStatus.BAD_REQUEST),
    RECORD_CIGARETTES_SMOKED_INVALID(5002, "Cigarettes smoked must be non-negative", HttpStatus.BAD_REQUEST),
    RECORD_DATE_REQUIRED(5003, "Date is required", HttpStatus.BAD_REQUEST),
    RECORD_DATE_INVALID(5004, "Date must be today or in the future", HttpStatus.BAD_REQUEST),
    RECORD_ALREADY_EXISTS(5005, "Record for this date already exists", HttpStatus.BAD_REQUEST),
    RECORD_ACCESS_DENIED(5006, "Access denied to this record", HttpStatus.FORBIDDEN),
    RECORD_UPDATE_FAILED(5007, "Failed to update record", HttpStatus.INTERNAL_SERVER_ERROR),
    RECORD_DELETE_FAILED(5008, "Failed to delete record", HttpStatus.INTERNAL_SERVER_ERROR);
    private int code;
    private String message;
    private HttpStatusCode statusCode;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.message = message;
        this.code = code;
        this.statusCode = statusCode;
    }
}