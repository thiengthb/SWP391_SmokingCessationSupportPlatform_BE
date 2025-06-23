package com.swpteam.smokingcessation.service.interfaces.identity;

import com.swpteam.smokingcessation.domain.dto.auth.request.*;
import com.swpteam.smokingcessation.domain.dto.auth.response.AuthenticationResponse;
import com.swpteam.smokingcessation.domain.dto.auth.response.GoogleTokenResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;

public interface IAuthenticationService {

    AuthenticationResponse googleLogin(GoogleLoginRequest request);

    AuthenticationResponse login(AuthenticationRequest request);

    AuthenticationResponse refreshingToken(String refreshToken);

    AuthenticationResponse register(RegisterRequest request);

    Authentication authenticate(String token);

    void sendResetPasswordEmail(String email);

    void resetPassword(ResetPasswordRequest request);

    void logout(String refreshToken);
}
