package com.swpteam.smokingcessation.constants;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
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
    ID_REQUIRED(1007, "ID is required", HttpStatus.BAD_REQUEST),
    ID_NOT_BLANK(1007, "ID cannot be blank", HttpStatus.BAD_REQUEST),

    // Authentication
    UNAUTHENTICATED(1000, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1001, "You do not have permission", HttpStatus.FORBIDDEN),
    TOKEN_EXPIRED(1003, "Token is expired", HttpStatus.BAD_REQUEST),
    WRONG_PASSWORD(1004, "Wrong password for email", HttpStatus.BAD_REQUEST),

    // Account
    ACCOUNT_NOT_EXISTED(1000, "Account does not exist", HttpStatus.BAD_REQUEST),
    ACCOUNT_EXISTED(1001, "Account already existed", HttpStatus.BAD_REQUEST),
    ACCOUNT_DELETED(1002, "Account has been deleted", HttpStatus.BAD_REQUEST),

    // Membership
    MEMBERSHIP_NOT_EXISTED(4000, "Membership does not exist", HttpStatus.BAD_REQUEST),
    MEMBERSHIP_NAME_UNIQUE(4001, "Membership name must be unique", HttpStatus.BAD_REQUEST),
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


    ;

    int code;
    String message;
    HttpStatusCode httpCode;
}
