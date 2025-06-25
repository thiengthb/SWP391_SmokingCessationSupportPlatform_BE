package com.swpteam.smokingcessation.utils;

import com.swpteam.smokingcessation.common.ApiResponse;
import com.swpteam.smokingcessation.constant.ErrorCode;
import com.swpteam.smokingcessation.constant.SuccessCode;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;

@Slf4j
@UtilityClass
public class ResponseUtil {

    public <T> ResponseEntity<ApiResponse<T>> buildSuccessResponse(SuccessCode code, T data) {
        return ResponseEntity
                .ok(
                        ApiResponse.<T>builder()
                                .code(code.getCode())
                                .message(code.getMessage())
                                .result(data)
                                .build()
                );
    }


    public ResponseEntity<ApiResponse<Void>> buildErrorResponse(ErrorCode errorCode, Exception exception) {

        String message = exception.getMessage() != null
                ? exception.getMessage()
                : errorCode.getMessage();

        log.error("{}: {}",
                errorCode.getMessage(),
                exception.getMessage(),
                exception
        );

        return ResponseEntity
                .status(errorCode.getHttpCode())
                .body(
                        ApiResponse.<Void>builder()
                                .code(errorCode.getCode())
                                .message(message)
                                .build()
                );
    }

}
