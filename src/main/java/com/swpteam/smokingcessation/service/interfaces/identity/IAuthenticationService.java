package com.swpteam.smokingcessation.service.interfaces.identity;

import com.nimbusds.jose.JOSEException;
import com.swpteam.smokingcessation.domain.dto.account.AccountResponse;
import com.swpteam.smokingcessation.domain.dto.auth.request.*;
import com.swpteam.smokingcessation.domain.dto.auth.response.AuthenticationResponse;
import com.swpteam.smokingcessation.domain.dto.auth.response.GoogleTokenResponse;
import com.swpteam.smokingcessation.domain.dto.auth.response.IntrospectResponse;

import java.text.ParseException;

public interface IAuthenticationService {

    GoogleTokenResponse getGoogleToken(GoogleTokenRequest request);

    AuthenticationResponse authenticate(AuthenticationRequest request);

    AuthenticationResponse refreshToken(String token) throws ParseException, JOSEException;

    AccountResponse register(RegisterRequest request);

    IntrospectResponse introspect(TokenRequest request) throws JOSEException, ParseException;

    void sendResetPasswordEmail(String email);

    void resetPassword(ResetPasswordRequest request);

    void logout() throws ParseException, JOSEException;
}
