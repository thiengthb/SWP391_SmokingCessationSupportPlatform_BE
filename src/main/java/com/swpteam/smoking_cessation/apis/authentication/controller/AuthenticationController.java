package com.swpteam.smoking_cessation.apis.authentication.controller;

import com.nimbusds.jose.JOSEException;
import com.swpteam.smoking_cessation.apis.authentication.dto.request.AuthenticationRequest;
import com.swpteam.smoking_cessation.apis.authentication.dto.request.GoogleTokenRequest;
import com.swpteam.smoking_cessation.apis.authentication.dto.request.IntrospectRequest;
import com.swpteam.smoking_cessation.apis.authentication.dto.response.ApiResponse;
import com.swpteam.smoking_cessation.apis.authentication.dto.response.AuthenticationResponse;
import com.swpteam.smoking_cessation.apis.authentication.dto.response.GoogleTokenResponse;
import com.swpteam.smoking_cessation.apis.authentication.dto.response.IntrospectResponse;
import com.swpteam.smoking_cessation.apis.authentication.service.AuthenticationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;


@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/auth")
public class AuthenticationController {
    AuthenticationService authenticationService;

    @PostMapping("/google/token")
    public ApiResponse<GoogleTokenResponse> getGoogleToken(@RequestBody GoogleTokenRequest request){
        var result = authenticationService.getGoogleToken(request);

        return ApiResponse.<GoogleTokenResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/token")
    public ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request){
        var result = authenticationService.authenticate(request);

        return ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/introspect")
    public ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest request) throws ParseException, JOSEException {
        var result = authenticationService.introspect(request);

        return ApiResponse.<IntrospectResponse>builder()
                .result(result)
                .build();
    }
}
