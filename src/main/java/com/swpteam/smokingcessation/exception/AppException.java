package com.swpteam.smokingcessation.exception;

import com.swpteam.smokingcessation.constant.ErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AppException extends RuntimeException {

    ErrorCode errorCode;
    String infoMessage;

    public AppException(ErrorCode errorCode) {
        super(errorCode.getMessageLocaleKey());
        this.errorCode = errorCode;
    }

    public AppException(ErrorCode errorCode, String infoMessage) {
        super(errorCode.getMessageLocaleKey() + " [MORE INFO]: " + infoMessage);
        this.errorCode = errorCode;
        this.infoMessage = infoMessage;
    }

}
