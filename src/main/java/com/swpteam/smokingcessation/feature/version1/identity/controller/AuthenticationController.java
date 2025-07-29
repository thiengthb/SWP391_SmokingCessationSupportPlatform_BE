package com.swpteam.smokingcessation.feature.version1.identity.controller;

import com.swpteam.smokingcessation.common.ApiResponse;
import com.swpteam.smokingcessation.constant.SuccessCode;
import com.swpteam.smokingcessation.domain.dto.auth.request.*;
import com.swpteam.smokingcessation.domain.dto.auth.response.AuthenticationResponse;
import com.swpteam.smokingcessation.feature.version1.identity.service.IAuthenticationService;
import com.swpteam.smokingcessation.utils.CookieUtil;
import com.swpteam.smokingcessation.utils.ResponseUtilService;
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
    ResponseUtilService responseUtilService;

    @PostMapping("/google/login")
    ResponseEntity<ApiResponse<AuthenticationResponse>> getGoogleToken(
            @RequestBody TokenRequest request,
            HttpServletResponse response
    ) {
        AuthenticationResponse authenticationResponse = authenticationService.googleLogin(request);

        cookieUtil.setRefreshTokenCookie(response, authenticationResponse.getRefreshToken());
        authenticationResponse.setRefreshToken(null);

        return responseUtilService.buildSuccessResponse(
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

        return responseUtilService.buildSuccessResponse(
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

        return responseUtilService.buildSuccessResponse(
                SuccessCode.TOKEN_REFRESH_SUCCESS,
                authenticationResponse
        );
    }

    @PostMapping("/register")
    ResponseEntity<ApiResponse<Void>> register(
            @RequestBody @Valid RegisterRequest request
    ) {
        authenticationService.register(request);

        return responseUtilService.buildSuccessResponse(
                SuccessCode.REGISTER_SUCCESS
        );
    }

    @PostMapping("/forgot-password")
    ResponseEntity<ApiResponse<Void>> forgotPassword(
            @RequestBody @Valid ForgotPasswordRequest request
    ) {
        authenticationService.sendResetPasswordEmail(request.email());

        return responseUtilService.buildSuccessResponse(
                SuccessCode.MAIL_SENT
        );
    }

    @PostMapping("/reset-password")
    ResponseEntity<ApiResponse<Void>> resetPassword(
            @RequestBody @Valid ResetPasswordRequest request
    ) {
        authenticationService.resetPassword(request);

        return responseUtilService.buildSuccessResponse(
                SuccessCode.PASSWORD_RESET_SUCCESS
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

        return responseUtilService.buildSuccessResponse(
                SuccessCode.LOGOUT_SUCCESS
        );
    }

}
