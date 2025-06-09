package com.swpteam.smokingcessation.exception;


import com.swpteam.smokingcessation.common.response.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse> handlingAppException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        ApiResponse apiResponse = new ApiResponse<>();

        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());

        return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleEntityNotFound(EntityNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ApiResponse> handleSecurityException(SecurityException ex) {
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setCode(401);
        apiResponse.setMessage(ex.getMessage());
        return new ResponseEntity<>(apiResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = getFirstErrorMessage(ex);

        ErrorCode errorCode = findErrorCodeByMessage(errorMessage);

        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());

        return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
    }

    private String getFirstErrorMessage(MethodArgumentNotValidException ex) {
        if (!ex.getBindingResult().getFieldErrors().isEmpty()) {
            return ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        } else if (!ex.getBindingResult().getGlobalErrors().isEmpty()) {
            return ex.getBindingResult().getGlobalErrors().get(0).getDefaultMessage();
        }
        return "INVALID_KEY";
    }

    private ErrorCode findErrorCodeByMessage(String message) {
        for (ErrorCode errorCode : ErrorCode.values()) {
            if (errorCode.name().equals(message)) {
                return errorCode;
            }
        }
        return ErrorCode.INVALID_KEY;
    }
}
    