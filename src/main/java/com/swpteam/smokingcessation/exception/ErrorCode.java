package com.swpteam.smokingcessation.exception;


import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    INVALID_KEY(999, "Uncategorized error", HttpStatus.BAD_REQUEST),
    INVALID_ENUM(1009,"Message type cannot be empty",HttpStatus.INTERNAL_SERVER_ERROR),
    //common
    CONTENT_REQUIRED(1001,"Message content cannot be empty",HttpStatus.BAD_REQUEST),
    MESSAGE_TYPE_REQUIRED(1006,"Message type cannot be empty",HttpStatus.BAD_REQUEST),
    EMAIL_REQUIRED(1000, "Email field cannot be empty", HttpStatus.BAD_REQUEST),

    //validation
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
    ;

    private int code;
    private String message;
    private HttpStatusCode statusCode;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.message = message;
        this.code = code;
        this.statusCode = statusCode;
    }
}
