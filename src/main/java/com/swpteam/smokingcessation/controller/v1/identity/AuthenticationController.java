package com.swpteam.smokingcessation.controller.v1.identity;

import com.swpteam.smokingcessation.common.ApiResponse;
import com.swpteam.smokingcessation.constant.SuccessCode;
import com.swpteam.smokingcessation.domain.dto.auth.request.*;
import com.swpteam.smokingcessation.domain.dto.auth.response.AuthenticationResponse;
import com.swpteam.smokingcessation.service.interfaces.identity.IAuthenticationService;
import com.swpteam.smokingcessation.utils.CookieUtil;
import com.swpteam.smokingcessation.utils.ResponseUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Authentication", description = "Manage authentication-related operations")
public class AuthenticationController {

    IAuthenticationService authenticationService;
    CookieUtil cookieUtil;

    @PostMapping("/google/login")
    ResponseEntity<ApiResponse<AuthenticationResponse>> getGoogleToken(
            @RequestBody GoogleLoginRequest request,
            HttpServletResponse response
    ) {
        AuthenticationResponse authenticationResponse = authenticationService.googleLogin(request);

        cookieUtil.setRefreshTokenCookie(response, authenticationResponse.getRefreshToken());
        authenticationResponse.setRefreshToken(null);

        return ResponseUtil.buildResponse(
                SuccessCode.GOOGLE_LOGIN_SUCCESS,
                authenticationResponse
        );
    }

    @PostMapping("/login")
    ResponseEntity<ApiResponse<AuthenticationResponse>> authenticate(
            @RequestBody @Valid AuthenticationRequest request,
            HttpServletResponse response
    ) {
        AuthenticationResponse authenticationResponse = authenticationService.login(request);

        cookieUtil.setRefreshTokenCookie(response, authenticationResponse.getRefreshToken());
        authenticationResponse.setRefreshToken(null);

        return ResponseUtil.buildResponse(
                SuccessCode.LOGIN_SUCCESS,
                authenticationResponse
        );
    }

    @PostMapping("/refresh-token")
    ResponseEntity<ApiResponse<AuthenticationResponse>> refreshToken(
            @CookieValue(value = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response
    ) {
        AuthenticationResponse authenticationResponse = authenticationService.refreshingToken(refreshToken);

        cookieUtil.setRefreshTokenCookie(response, authenticationResponse.getRefreshToken());
        authenticationResponse.setRefreshToken(null);

        return ResponseUtil.buildResponse(
                SuccessCode.TOKEN_REFRESH_SUCCESS,
                authenticationResponse
        );
    }

    @PostMapping("/register")
    ResponseEntity<ApiResponse<AuthenticationResponse>> register(
            @RequestBody @Valid RegisterRequest request,
            HttpServletResponse response
    ) {
        AuthenticationResponse authenticationResponse = authenticationService.register(request);

        cookieUtil.setRefreshTokenCookie(response, authenticationResponse.getRefreshToken());
        authenticationResponse.setRefreshToken(null);

        return ResponseUtil.buildResponse(
                SuccessCode.REGISTER_SUCCESS,
                authenticationResponse
        );
    }

    @PostMapping("/forgot-password")
    ResponseEntity<ApiResponse<Void>> forgotPassword(
            @RequestBody @Valid ForgotPasswordRequest request
    ) {
        authenticationService.sendResetPasswordEmail(request.email());

        return ResponseUtil.buildResponse(
                SuccessCode.SEND_MAIL_SUCCESS,
                null
        );
    }

    @PostMapping("/reset-password")
    ResponseEntity<ApiResponse<Void>> resetPassword(
            @RequestBody @Valid ResetPasswordRequest request
    ) {
        authenticationService.resetPassword(request);

        return ResponseUtil.buildResponse(
                SuccessCode.PASSWORD_RESET_SUCCESS,
                null
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String refreshToken = cookieUtil.getRefreshTokenFromCookie(request)
                .orElse(null);

        if (refreshToken != null) {
            authenticationService.logout(refreshToken);
            cookieUtil.clearRefreshTokenCookie(response);
        }

        return ResponseUtil.buildResponse(
                SuccessCode.LOGOUT_SUCCESS,
                null
        );
    }

}
