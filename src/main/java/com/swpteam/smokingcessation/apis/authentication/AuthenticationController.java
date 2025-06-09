package com.swpteam.smokingcessation.apis.authentication;

import com.nimbusds.jose.JOSEException;
import com.swpteam.smokingcessation.apis.account.dto.response.AccountResponse;
import com.swpteam.smokingcessation.apis.authentication.dto.request.*;
import com.swpteam.smokingcessation.apis.authentication.dto.response.AuthenticationResponse;
import com.swpteam.smokingcessation.apis.authentication.dto.response.GoogleTokenResponse;
import com.swpteam.smokingcessation.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;


@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/auth")
public class AuthenticationController {
    AuthenticationService authenticationService;

    @PostMapping("/google/login")
    public ApiResponse<GoogleTokenResponse> getGoogleToken(@RequestBody GoogleTokenRequest request) {
        var result = authenticationService.getGoogleToken(request);

        return ApiResponse.<GoogleTokenResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> authenticate(@RequestBody @Valid AuthenticationRequest request) {
        var result = authenticationService.authenticate(request);

        // Remove refreshToken from body
        String refreshToken = result.getRefreshToken();
        result.setRefreshToken(null);

        return ResponseEntity.ok()
                .header("X-Refresh-Token", refreshToken)
                .body(ApiResponse.<AuthenticationResponse>builder()
                        .result(result)
                        .build());
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> refreshToken(@RequestBody @Valid RefreshTokenRequest request) throws ParseException, JOSEException {
        var result = authenticationService.refreshToken(request);

        // Remove refreshToken from body
        String refreshToken = result.getRefreshToken();
        result.setRefreshToken(null);

        return ResponseEntity.ok()
                .header("X-Refresh-Token", refreshToken)
                .body(ApiResponse.<AuthenticationResponse>builder()
                        .result(result)
                        .build());
    }

    @PostMapping("/register")
    public ApiResponse<AccountResponse> register(@RequestBody @Valid RegisterRequest request) {
        var result = authenticationService.register(request);
        return ApiResponse.<AccountResponse>builder()
                .result(result)
                .build();
    }


    @PostMapping("/forgot-password")
    public ApiResponse<String> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) {
        authenticationService.sendResetPasswordEmail(request.getEmail());
        return ApiResponse.<String>builder()
                .result("Reset password link sent to your email if it exists in our system.")
                .build();
    }

    @PostMapping("/reset-password")
    public ApiResponse<String> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        authenticationService.resetPassword(request);

        return ApiResponse.<String>builder()
                .result("Password has been reset successfully.")
                .build();
    }
}
