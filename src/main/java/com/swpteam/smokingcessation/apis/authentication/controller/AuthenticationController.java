package com.swpteam.smokingcessation.apis.authentication.controller;

import com.nimbusds.jose.JOSEException;
import com.swpteam.smokingcessation.apis.account.dto.response.AccountResponse;
import com.swpteam.smokingcessation.apis.authentication.dto.request.AuthenticationRequest;
import com.swpteam.smokingcessation.apis.authentication.dto.request.GoogleTokenRequest;
import com.swpteam.smokingcessation.apis.authentication.dto.request.RefreshTokenRequest;
import com.swpteam.smokingcessation.apis.authentication.dto.request.RegisterRequest;
import com.swpteam.smokingcessation.apis.authentication.dto.response.AuthenticationResponse;
import com.swpteam.smokingcessation.apis.authentication.dto.response.GoogleTokenResponse;
import com.swpteam.smokingcessation.apis.authentication.service.AuthenticationService;
import com.swpteam.smokingcessation.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
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
    public ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        var result = authenticationService.authenticate(request);

        return ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/refresh")
    public ApiResponse<AuthenticationResponse> refreshToken(@RequestBody RefreshTokenRequest request) throws ParseException, JOSEException {
        var result = authenticationService.refreshToken(request);

        return ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/register")
    public ApiResponse<AccountResponse> register(@RequestBody @Valid RegisterRequest request) {
        var result = authenticationService.register(request);
        return ApiResponse.<AccountResponse>builder()
                .result(result)
                .build();
    }
}
