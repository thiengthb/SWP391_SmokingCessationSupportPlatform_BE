package com.swpteam.smokingcessation.exception;

public enum ErrorCode {
    USER_NOTEXIST (100,"User does not exist in the system")
    ;

    private int code;
    private String message;

    ErrorCode(int code, String message) {
        this.message = message;
        this.code = code;
    }

    public
    void setMessage(String message) {
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
