package com.swpteam.smokingcessation.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
public enum ErrorCode {
    USER_NOT_EXISTED(1000,"User does not exist"),
    MEMBERSHIP_NOT_EXISTED(1001, "Membership does not exist")
    ;

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.message = message;
        this.code = code;
    }

}
