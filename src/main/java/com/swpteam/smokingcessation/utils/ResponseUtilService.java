package com.swpteam.smokingcessation.utils;

import com.swpteam.smokingcessation.common.ApiResponse;
import com.swpteam.smokingcessation.constant.ErrorCode;
import com.swpteam.smokingcessation.constant.SuccessCode;
import com.swpteam.smokingcessation.feature.version1.internalization.MessageSourceService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ResponseUtilService {

    MessageSourceService messageSourceService;

    public <T> ResponseEntity<ApiResponse<T>> buildSuccessResponse(SuccessCode successCode, T data) {
        String localeMessage = messageSourceService.getSuccessLocalizeMessage(successCode);

        return ResponseEntity
                .ok(
                        ApiResponse.<T>builder()
                                .code(successCode.getCode())
                                .message(localeMessage)
                                .result(data)
                                .build()
                );
    }

    public ResponseEntity<ApiResponse<Void>> buildSuccessResponse(SuccessCode successCode) {
        return buildSuccessResponse(successCode, null);
    }

    public ResponseEntity<ApiResponse<Void>> buildErrorResponse(ErrorCode errorCode, Exception exception) {

        String message = exception.getMessage() != null
                ? exception.getMessage()
                : messageSourceService.getErrorLocalizeMessage(errorCode);

        log.error("{}: {}",
                messageSourceService.getErrorLocalizeMessage(errorCode),
                exception.getMessage(),
                exception
        );

        return ResponseEntity
                .status(errorCode.getHttpCode())
                .body(
                        ApiResponse.<Void>builder()
                                .code(errorCode.getCode())
                                .message( messageSourceService.getErrorLocalizeMessage(errorCode))
                                .build()
                );
    }

}
