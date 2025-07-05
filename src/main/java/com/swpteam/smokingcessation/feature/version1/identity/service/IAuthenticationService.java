package com.swpteam.smokingcessation.feature.version1.identity.service;

import com.swpteam.smokingcessation.domain.dto.auth.request.*;
import com.swpteam.smokingcessation.domain.dto.auth.response.AuthenticationResponse;
import org.springframework.security.core.Authentication;

public interface IAuthenticationService {

    AuthenticationResponse googleLogin(TokenRequest request);

    AuthenticationResponse login(AuthenticationRequest request);

    AuthenticationResponse refreshingToken(String refreshToken);

    void register(RegisterRequest request);

    Authentication authenticate(String token);

    void sendResetPasswordEmail(String email);

    void resetPassword(ResetPasswordRequest request);

    void sendEmailVerification(String email);

    boolean verifyToActivateAccount(String token);

    void logout(String refreshToken);
}
