package com.swpteam.smokingcessation.exception;


public enum ErrorCode {
    ACCOUNT_NOT_EXISTED(100, "Account does not exist in the system"),
    ACCOUNT_EXISTED(101, "Account already exist in the system"),
    ACCOUNT_DELETED(102, "Account has been deleted"),
    TOKEN_EXPIRED(103, "Token is expired"),
    WRONG_PASSWORD(104, "Wrong password for email");

    private int code;
    private String message;

    ErrorCode(int code, String message) {
        this.message = message;
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
