package com.swpteam.smokingcessation.controller.v1.identity;

import com.nimbusds.jose.JOSEException;
import com.swpteam.smokingcessation.common.ApiResponse;
import com.swpteam.smokingcessation.constant.SuccessCode;
import com.swpteam.smokingcessation.domain.dto.account.AccountResponse;
import com.swpteam.smokingcessation.domain.dto.auth.request.*;
import com.swpteam.smokingcessation.domain.dto.auth.response.AuthenticationResponse;
import com.swpteam.smokingcessation.domain.dto.auth.response.GoogleTokenResponse;
import com.swpteam.smokingcessation.service.interfaces.identity.IAuthenticationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Authentication", description = "Manage authentication-related operations")
public class AuthenticationController {

    IAuthenticationService authenticationService;

    @PostMapping("/google/login")
    ResponseEntity<ApiResponse<GoogleTokenResponse>> getGoogleToken(@RequestBody GoogleTokenRequest request) {
        return ResponseEntity.ok(
                ApiResponse.<GoogleTokenResponse>builder()
                        .code(SuccessCode.GOOGLE_LOGIN_SUCCESS.getCode())
                        .message(SuccessCode.GOOGLE_LOGIN_SUCCESS.getMessage())
                        .result(authenticationService.getGoogleToken(request))
                        .build());
    }

    @PostMapping("/login")
    ResponseEntity<ApiResponse<AuthenticationResponse>> authenticate(@RequestBody @Valid AuthenticationRequest request) {
        return ResponseEntity.ok()
                .body(ApiResponse.<AuthenticationResponse>builder()
                        .code(SuccessCode.LOGIN_SUCCESS.getCode())
                        .message(SuccessCode.LOGIN_SUCCESS.getMessage())
                        .result(authenticationService.authenticate(request))
                        .build());
    }

    @PostMapping("/refresh")
    ResponseEntity<ApiResponse<AuthenticationResponse>> refreshToken(@AuthenticationPrincipal Jwt jwt) throws ParseException, JOSEException {
        String token = jwt.getTokenValue();
        return ResponseEntity.ok()
                .body(ApiResponse.<AuthenticationResponse>builder()
                        .code(SuccessCode.TOKEN_REFRESH_SUCCESS.getCode())
                        .message(SuccessCode.TOKEN_REFRESH_SUCCESS.getMessage())
                        .result(authenticationService.refreshToken(token))
                        .build());
    }

    @PostMapping("/register")
    ResponseEntity<ApiResponse<AccountResponse>> register(@RequestBody @Valid RegisterRequest request) {
        return ResponseEntity.ok(
                ApiResponse.<AccountResponse>builder()
                        .code(SuccessCode.REGISTER_SUCCESS.getCode())
                        .message(SuccessCode.REGISTER_SUCCESS.getMessage())
                        .result(authenticationService.register(request))
                        .build());
    }


    @PostMapping("/forgot-password")
    ResponseEntity<ApiResponse<String>> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) {
        authenticationService.sendResetPasswordEmail(request.getEmail());
        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .code(SuccessCode.SEND_MAIL_SUCCESS.getCode())
                        .message(SuccessCode.SEND_MAIL_SUCCESS.getMessage())
                        .result("Reset password link sent to your email if it exists in our system.")
                        .build());
    }

    @PostMapping("/reset-password")
    ResponseEntity<ApiResponse<Void>> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        authenticationService.resetPassword(request);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .code(SuccessCode.PASSWORD_RESET_SUCCESS.getCode())
                        .message(SuccessCode.PASSWORD_RESET_SUCCESS.getMessage())
                        .build());
    }

    //front-end job
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() throws JOSEException, ParseException {
        authenticationService.logout();
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .code(SuccessCode.LOGOUT_SUCCESS.getCode())
                        .message(SuccessCode.LOGOUT_SUCCESS.getMessage())
                        .build()
        );
    }


}
