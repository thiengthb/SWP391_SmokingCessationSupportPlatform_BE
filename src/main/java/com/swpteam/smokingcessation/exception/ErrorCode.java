package com.swpteam.smokingcessation.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
public enum ErrorCode {
    ACCOUNT_NOT_EXISTED(100, "Account does not exist in the system"),
    ACCOUNT_EXISTED(101, "Account already exist in the system"),
    ACCOUNT_DELETED(102, "Account has been deleted"),
    TOKEN_EXPIRED(103, "Token is expired"),
    WRONG_PASSWORD(104, "Wrong password for email"),
    MEMBERSHIP_NOT_EXISTED(105, "Membership does not exist")
    ;

    private int code;
    private String message;

    ErrorCode(int code, String message) {
        this.message = message;
        this.code = code;
    }

}
