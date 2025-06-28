package com.swpteam.smokingcessation.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swpteam.smokingcessation.common.ApiResponse;
import com.swpteam.smokingcessation.constant.ErrorCode;
import com.swpteam.smokingcessation.utils.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {



    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException)
            throws IOException {

        ErrorCode errorCode = getErrorCode(authException);

        response.setStatus(errorCode.getHttpCode().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ResponseEntity<ApiResponse<Void>> errorResponse = ResponseUtil.buildErrorResponse(errorCode, authException);

        ObjectMapper objectMapper = new ObjectMapper();

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        response.flushBuffer();
    }

    @NotNull
    private static ErrorCode getErrorCode(AuthenticationException authException) {
        ErrorCode errorCode = ErrorCode.UNAUTHENTICATED;

        String causeMsg = null;
        if (authException.getCause() != null && authException.getCause().getMessage() != null) {
            causeMsg = authException.getCause().getMessage();
        } else if (authException.getMessage() != null) {
            causeMsg = authException.getMessage();
        }

        if (causeMsg != null && causeMsg.toLowerCase().contains("expired")) {
            errorCode = ErrorCode.TOKEN_EXPIRED;
        }
        return errorCode;
    }

}
