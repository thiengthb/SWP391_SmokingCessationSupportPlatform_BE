package com.swpteam.smokingcessation.exception;


import com.nimbusds.jose.JOSEException;
import com.nimbusds.oauth2.sdk.ParseException;
import com.swpteam.smokingcessation.common.ApiResponse;
import com.swpteam.smokingcessation.constant.ErrorCode;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.Map;
import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String MIN_ATTRIBUTE = "min";

    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse<Void>> handlingException(Exception exception) {
        log.error("Unexpected exception: {}", exception.getMessage(), exception);

        return ResponseEntity.badRequest().body(
                ApiResponse.<Void>builder()
                        .code(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode())
                        .message(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse<Void>> handlingAppException(AppException exception) {
        log.warn("AppException: {} - {}", exception.getErrorCode(), exception.getMessage(), exception);

        ErrorCode errorCode = exception.getErrorCode();

        return ResponseEntity.status(errorCode.getHttpCode()).body(
                ApiResponse.<Void>builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(CurrencyRateException.class)
    public ResponseEntity<ApiResponse<Void>> handleCurrencyRateException(CurrencyRateException exception) {
        log.error("Currency rate update error: {}", exception.getMessage(), exception);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponse.<Void>builder()
                        .code(ErrorCode.CURRENCY_RATE_ERROR.getCode())
                        .message(exception.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(EntityNotFoundException.class)
    ResponseEntity<String> handleEntityNotFoundException(EntityNotFoundException exception) {
        log.warn("EntityNotFoundException: {}", exception.getMessage(), exception);

        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<ApiResponse<Void>> handlingAccessDeniedException(AccessDeniedException exception) {
        log.warn("AccessDeniedException: {}", exception.getMessage(), exception);

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                ApiResponse.<Void>builder()
                        .code(ErrorCode.FORBIDDEN.getCode())
                        .message(ErrorCode.FORBIDDEN.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(SecurityException.class)
    ResponseEntity<ApiResponse<Void>> handleSecurityException(SecurityException exception) {
        log.warn("SecurityException: {}", exception.getMessage(), exception);

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ApiResponse.<Void>builder()
                        .code(401)
                        .message(exception.getMessage())
                        .build()
        );
    }

    @ExceptionHandler({JOSEException.class, ParseException.class})
    public ResponseEntity<ApiResponse<Void>> handleJwtParsingException(Exception exception) {
        log.warn("Token parsing/validation failed: {}", exception.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ApiResponse.<Void>builder()
                        .code(ErrorCode.UNAUTHENTICATED.getCode())
                        .message(exception.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidBody(HttpMessageNotReadableException ex) {
        if (ex.getCause() instanceof com.fasterxml.jackson.databind.exc.InvalidFormatException &&
                ex.getCause().getCause() instanceof java.time.format.DateTimeParseException dtpe) {

            log.warn("Invalid date format: {}", dtpe.getParsedString());

            return ResponseEntity.badRequest().body(
                    ApiResponse.<Void>builder()
                            .code(ErrorCode.INVALID_DATE_FORMAT.getCode())
                            .message(ErrorCode.INVALID_DATE_FORMAT.getMessage())
                            .build()
            );
        }
        return ResponseEntity.badRequest().body(
                ApiResponse.<Void>builder()
                        .code(400)
                        .message("Malformed or missing request body")
                        .build()
        );
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<?> handleValidationException(HandlerMethodValidationException exception) {
        ErrorCode errorCode = ErrorCode.INVALID_MESSAGE_KEY;
        try {
            MessageSourceResolvable firstError = exception.getAllErrors().getFirst();
            String enumKey = firstError.getDefaultMessage();

            errorCode = ErrorCode.valueOf(enumKey);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid enum key:", e);
        }

        return ResponseEntity.badRequest().body(
                ApiResponse.<Void>builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse<Void>> handlingValidation(MethodArgumentNotValidException exception) {
        log.warn("Validation failed: {}", exception.getMessage(), exception);

        String enumKey = Objects.requireNonNull(exception.getFieldError()).getDefaultMessage();

        ErrorCode errorCode = ErrorCode.INVALID_MESSAGE_KEY;
        Map<String, Object> attributes = null;
        try {
            errorCode = ErrorCode.valueOf(enumKey);

            var constraintViolation =
                    exception.getBindingResult().getAllErrors().getFirst().unwrap(ConstraintViolation.class);

            attributes = constraintViolation.getConstraintDescriptor().getAttributes();

            log.info(attributes.toString());

        } catch (IllegalArgumentException e) {
            log.warn("Invalid enum key: {}", enumKey, e);
        }

        return ResponseEntity.badRequest().body(
                ApiResponse.<Void>builder()
                        .code(errorCode.getCode())
                        .message(Objects.nonNull(attributes)
                                ? mapAttribute(errorCode.getMessage(), attributes)
                                : errorCode.getMessage())
                        .build()
        );
    }

    private String mapAttribute(String message, Map<String, Object> attributes) {
        String minValue = String.valueOf(attributes.get(MIN_ATTRIBUTE));

        return message.replace("{" + MIN_ATTRIBUTE + "}", minValue);
    }
}
    