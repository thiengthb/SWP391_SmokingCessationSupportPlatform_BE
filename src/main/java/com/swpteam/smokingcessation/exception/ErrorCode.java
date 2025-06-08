package com.swpteam.smokingcessation.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_MESSAGE_KEY(9999, "Invalid message key", HttpStatus.INTERNAL_SERVER_ERROR),

    // Common
    ACCOUNT_REQUIRED(1000, "Account is required", HttpStatus.BAD_REQUEST),
    EMAIL_REQUIRED(1001, "Email is required", HttpStatus.BAD_REQUEST),
    INVALID_EMAIL_FORMAT(1002, "Wrong email format", HttpStatus.BAD_REQUEST),
    START_DATE_REQUIRED(1003, "Start date is required", HttpStatus.BAD_REQUEST),
    END_DATE_REQUIRED(1004, "End date is required", HttpStatus.BAD_REQUEST),
    PAYMENT_STATUS_REQUIRED(1005, "Payment status is required", HttpStatus.BAD_REQUEST),
    ACCOUNT_NOT_BLANK(1006, "Account ID cannot be blank", HttpStatus.BAD_REQUEST),
    PASSWORD_REQUIRED(1007, "Password field cannot be empty", HttpStatus.BAD_REQUEST),
    CODE_REQUIRED(1008, "Google code field cannot be empty", HttpStatus.BAD_REQUEST),
    REFRESH_TOKEN_REQUIRED(1009, "Refresh token field cannot be empty", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1010, "Password must be at least {min} characters", HttpStatus.BAD_REQUEST),
    PHONE_NUMBER_INVALID(1011, "Phone number must be 10 digits and consists of numbers only", HttpStatus.BAD_REQUEST),

    // Authentication
    UNAUTHENTICATED(2000, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(2001, "You do not have permission", HttpStatus.FORBIDDEN),
    TOKEN_EXPIRED(2002, "Token is expired", HttpStatus.BAD_REQUEST),
    WRONG_PASSWORD(2003, "Wrong password for email", HttpStatus.BAD_REQUEST),
    INVALID_SIGNATURE(2004, "Token signature is invalid", HttpStatus.BAD_REQUEST),
    EMAIL_SEND_FAILED(2005, "Failed to send email. Please try again later.", HttpStatus.INTERNAL_SERVER_ERROR),

    // Account
    ACCOUNT_NOT_EXISTED(3000, "Account does not exist", HttpStatus.BAD_REQUEST),
    ACCOUNT_EXISTED(3001, "Account already existed", HttpStatus.BAD_REQUEST),
    ACCOUNT_DELETED(3002, "Account has been deleted", HttpStatus.BAD_REQUEST),

    // Membership
    MEMBERSHIP_NOT_EXISTED(4000, "Membership does not exist", HttpStatus.BAD_REQUEST),
    MEMBERSHIP_EXISTED(4001, "Membership already existed", HttpStatus.BAD_REQUEST),
    MEMBERSHIP_NAME_NOT_EMPTY(4002, "Membership name must not be empty", HttpStatus.BAD_REQUEST),
    MEMBERSHIP_MIN_SIZE(4003, "Membership name length must be at least {min} characters", HttpStatus.BAD_REQUEST),
    DURATION_NEGATIVE(4004, "Duration must be a positive number", HttpStatus.BAD_REQUEST),
    PRICE_NEGATIVE(4005, "Price must be a positive number", HttpStatus.BAD_REQUEST),

    // Transaction
    AMOUNT_REQUIRED(5001, "Transaction amount is required", HttpStatus.BAD_REQUEST),
    NAME_REQUIRED(5002, "Name is required", HttpStatus.BAD_REQUEST),
    CURRENCY_REQUIRED(5003, "Currency is required", HttpStatus.BAD_REQUEST),
    AMOUNT_NEGATIVE(5004, "Transaction amount must be a positive number", HttpStatus.BAD_REQUEST),

    // Subscription
    SUBSCRIPTION_NOT_EXISTED(4000, "Subscription does not exist", HttpStatus.BAD_REQUEST),
    START_DATE_MUST_BE_TODAY_OR_FUTURE(6001, "Start date must be today or in the future", HttpStatus.BAD_REQUEST),
    END_DATE_MUST_BE_IN_FUTURE(6002, "End date must be today or in the future", HttpStatus.BAD_REQUEST),

    // Setting
    THEME_REQUIRED(7000, "Theme is required", HttpStatus.BAD_REQUEST),
    LANGUAGE_REQUIRED(7001, "Language is required", HttpStatus.BAD_REQUEST),
    TRACKING_MODE_REQUIRED(7002, "Tracking mode is required", HttpStatus.BAD_REQUEST),
    MOTIVATION_REQUIRED(7003, "Motivation per day is required", HttpStatus.BAD_REQUEST),
    MOTIVATION_MIN(7004, "Motivation per day must be at least 1", HttpStatus.BAD_REQUEST),
    MOTIVATION_MAX(7005, "Motivation per day must be at most 100", HttpStatus.BAD_REQUEST),
    DEADLINE_REQUIRED(7006, "Report deadline is required", HttpStatus.BAD_REQUEST),

    //Member
    MEMBER_EXISTED(8000, "Member fields already exist", HttpStatus.BAD_REQUEST),
    MEMBER_NOT_EXISTED(8001, "Member doesn't exist", HttpStatus.BAD_REQUEST),
    ;
    private final int code;
    private final String message;
    private final HttpStatusCode httpCode;

}
