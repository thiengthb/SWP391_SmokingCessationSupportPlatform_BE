package com.swpteam.smokingcessation.exception;


import com.nimbusds.jose.JOSEException;
import com.nimbusds.oauth2.sdk.ParseException;
import com.swpteam.smokingcessation.common.ApiResponse;
import com.swpteam.smokingcessation.constant.ErrorCode;
import com.swpteam.smokingcessation.feature.version1.internalization.MessageSourceService;
import com.swpteam.smokingcessation.utils.ResponseUtilService;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSourceResolvable;
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
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GlobalExceptionHandler {

    private static final String MIN_ATTRIBUTE = "min";

    ResponseUtilService responseUtilService;
    MessageSourceService messageSourceService;

    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse<Void>> handlingException(Exception exception) {
        return responseUtilService
                .buildErrorResponse(ErrorCode.UNCATEGORIZED_ERROR, exception);
    }

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse<Void>> handlingAppException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();

        return responseUtilService
                .buildErrorResponse(errorCode, exception);
    }

    @ExceptionHandler(value = MessagingException.class)
    ResponseEntity<ApiResponse<Void>> handlingMessagingException(MessagingException exception) {
        return responseUtilService
                .buildErrorResponse(ErrorCode.UNCATEGORIZED_ERROR, exception);
    }


    @ExceptionHandler(value = CurrencyRateException.class)
    public ResponseEntity<ApiResponse<Void>> handleCurrencyRateException(CurrencyRateException exception) {
        return responseUtilService
                .buildErrorResponse(ErrorCode.CURRENCY_RATE_UPDATE_FAILED, exception);
    }

    @ExceptionHandler(value = EntityNotFoundException.class)
    ResponseEntity<ApiResponse<Void>> handleEntityNotFoundException(EntityNotFoundException exception) {
        return responseUtilService
                .buildErrorResponse(ErrorCode.ENTITY_NOT_FOUND, exception);
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<ApiResponse<Void>> handlingAccessDeniedException(AccessDeniedException exception) {
        return responseUtilService
                .buildErrorResponse(ErrorCode.UNAUTHORIZED, exception);
    }

    @ExceptionHandler(value = SecurityException.class)
    ResponseEntity<ApiResponse<Void>> handleSecurityException(SecurityException exception) {
        return responseUtilService
                .buildErrorResponse(ErrorCode.UNAUTHORIZED, exception);
    }

    @ExceptionHandler(value = {JOSEException.class, ParseException.class})
    public ResponseEntity<ApiResponse<Void>> handleJwtParsingException(Exception exception) {
        log.error("Token parsing / Validation failed: {}", exception.getMessage());

        return responseUtilService
                .buildErrorResponse(ErrorCode.UNAUTHENTICATED, exception);
    }

    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidBody(HttpMessageNotReadableException exception) {
        return responseUtilService
                .buildErrorResponse(ErrorCode.INVALID_REQUEST_BODY, exception);
    }

    @ExceptionHandler(value = HandlerMethodValidationException.class)
    public ResponseEntity<?> handleValidationException(HandlerMethodValidationException exception) {
        ErrorCode errorCode = ErrorCode.INVALID_MESSAGE_KEY;
        try {
            MessageSourceResolvable firstError = exception.getAllErrors().getFirst();
            String enumKey = firstError.getDefaultMessage();

            errorCode = ErrorCode.valueOf(enumKey);
        } catch (IllegalArgumentException e) {
            log.error("Invalid enum key:", e);
        }

        return responseUtilService.buildErrorResponse(errorCode, exception);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse<Void>> handlingValidation(MethodArgumentNotValidException exception) {
        log.error("Validation failed: {}", exception.getMessage(), exception);

        ErrorCode errorCode = ErrorCode.INVALID_MESSAGE_KEY;

        String enumKey = exception.getBindingResult().getAllErrors().stream()
                .map(error -> error.getDefaultMessage())
                .filter(Objects::nonNull)
                .findFirst()
                .orElse("INVALID_MESSAGE_KEY");
        Map<String, Object> attributes = null;
        try {
            errorCode = ErrorCode.valueOf(enumKey);

            var constraintViolation =
                    exception.getBindingResult().getAllErrors().getFirst().unwrap(ConstraintViolation.class);

            attributes = constraintViolation.getConstraintDescriptor().getAttributes();

            log.info(attributes.toString());

        } catch (IllegalArgumentException e) {
            log.error("Invalid enum key: {}", enumKey, e);
        }

        String message = messageSourceService.getErrorLocalizeMessage(errorCode);

        return ResponseEntity.badRequest()
                .body(
                        ApiResponse.<Void>builder()
                                .code(errorCode.getCode())
                                .message(Objects.nonNull(attributes)
                                        ? mapAttribute(message, attributes)
                                        : message)
                                .build()
                );
    }

    private String mapAttribute(String message, Map<String, Object> attributes) {
        String minValue = String.valueOf(attributes.get(MIN_ATTRIBUTE));

        return message.replace("{" + MIN_ATTRIBUTE + "}", minValue);
    }
}
    