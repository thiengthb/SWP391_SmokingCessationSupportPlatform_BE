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
    EMAIL_FORMAT(1002, "Wrong email format", HttpStatus.BAD_REQUEST),

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
    MEMBERSHIP_EXISTED(4001, "Membership already existed", HttpStatus.BAD_REQUEST),
    MEMBERSHIP_NAME_NOT_EMPTY(4002, "Membership name must not be empty", HttpStatus.BAD_REQUEST),
    MEMBERSHIP_MIN_SIZE(4003, "Membership name length must be at least 3 characters", HttpStatus.BAD_REQUEST),
    DURATION_NEGATIVE(4004, "Duration must be a positive number", HttpStatus.BAD_REQUEST),
    PRICE_NEGATIVE(4005, "Price must be a positive number", HttpStatus.BAD_REQUEST),

    // Transaction
    AMOUNT_REQUIRED(5002, "Transaction amount is required", HttpStatus.BAD_REQUEST),
    NAME_REQUIRED(5003, "Name is required", HttpStatus.BAD_REQUEST),
    CURRENCY_REQUIRED(5004, "Currency is required", HttpStatus.BAD_REQUEST),
    AMOUNT_NEGATIVE(5005, "Transaction amount must be a positive number", HttpStatus.BAD_REQUEST),


    ;
    private final int code;
    private final String message;
    private final HttpStatusCode httpCode;

}
