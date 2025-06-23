package com.swpteam.smokingcessation.utils;

import com.swpteam.smokingcessation.common.ApiResponse;
import com.swpteam.smokingcessation.constant.SuccessCode;
import lombok.experimental.UtilityClass;
import org.springframework.http.ResponseEntity;

@UtilityClass
public class ResponseUtil {

    public <T> ResponseEntity<ApiResponse<T>> buildResponse(SuccessCode code, T data) {
        return ResponseEntity.ok(
                ApiResponse.<T>builder()
                        .code(code.getCode())
                        .message(code.getMessage())
                        .result(data)
                        .build());
    }
    
}
