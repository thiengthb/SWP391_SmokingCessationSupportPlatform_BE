package com.swpteam.smokingcessation.service.impl.identity;

import com.nimbusds.jose.*;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.swpteam.smokingcessation.domain.dto.auth.request.*;
import com.swpteam.smokingcessation.domain.entity.Account;
import com.swpteam.smokingcessation.domain.mapper.AccountMapper;
import com.swpteam.smokingcessation.integration.mail.IMailService;
import com.swpteam.smokingcessation.repository.AccountRepository;
import com.swpteam.smokingcessation.domain.dto.account.AccountResponse;
import com.swpteam.smokingcessation.domain.enums.AccountStatus;
import com.swpteam.smokingcessation.domain.enums.Role;
import com.swpteam.smokingcessation.domain.dto.auth.response.AuthenticationResponse;
import com.swpteam.smokingcessation.domain.dto.auth.response.GoogleTokenResponse;
import com.swpteam.smokingcessation.domain.dto.auth.response.IntrospectResponse;
import com.swpteam.smokingcessation.repository.RefreshTokenRepository;
import com.swpteam.smokingcessation.domain.entity.Setting;
import com.swpteam.smokingcessation.constant.ErrorCode;
import com.swpteam.smokingcessation.domain.entity.RefreshToken;
import com.swpteam.smokingcessation.exception.AppException;
import com.swpteam.smokingcessation.service.interfaces.identity.IAuthenticationService;
import com.swpteam.smokingcessation.utils.AuthUtil;
import com.swpteam.smokingcessation.utils.JwtUtil;
import jakarta.mail.MessagingException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.text.ParseException;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationServiceImpl implements IAuthenticationService {

    AccountMapper accountMapper;
    AccountRepository accountRepository;
    RefreshTokenRepository refreshTokenRepository;

    WebClient webClient = WebClient.create();
    PasswordEncoder passwordEncoder;

    IMailService mailService;
    AuthUtil authUtil;
    JwtUtil jwtUtil;

    @NonFinal
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    protected String CLIENT_ID;

    @NonFinal
    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    protected String CLIENT_SECRET;

    @NonFinal
    @Value("${spring.security.oauth2.client.registration.google.redirect-uri:http://localhost:8080/oauth2/callback}")
    protected String REDIRECT_URI;

    @NonFinal
    @Value("${app.frontend-domain}")
    protected String FRONTEND_DOMAIN;

    @Override
    public GoogleTokenResponse getGoogleToken(GoogleTokenRequest request) {
        String tokenEndpoint = "https://oauth2.googleapis.com/token";
        Mono<GoogleTokenResponse> responseMono = webClient.post()
                .uri(tokenEndpoint)
                .bodyValue(
                        "code=" + request.getCode() +
                                "&client_id=" + CLIENT_ID +
                                "&client_secret=" + CLIENT_SECRET +
                                "&redirect_uri=" + REDIRECT_URI +
                                "&grant_type=authorization_code"
                )
                .header("Content-Type", "application/x-www-form-urlencoded")
                .retrieve()
                .bodyToMono(GoogleTokenResponse.class);

        return responseMono.block();
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        Account account = accountRepository.findByEmailAndIsDeletedFalse(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));

        boolean authenticated = passwordEncoder.matches(request.getPassword(), account.getPassword());
        if (!authenticated) {
            throw new AppException(ErrorCode.WRONG_PASSWORD);
        }

        String accessToken = jwtUtil.generateAccessToken(account);
        AccountResponse accountResponse = accountMapper.toResponse(account);

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .accountResponse(accountResponse)
                .build();
    }



    @Override
    @Transactional
    public AuthenticationResponse refreshToken(String token) throws ParseException, JOSEException {
        var signedJWT = jwtUtil.verifyToken(token, true);

        var jit = signedJWT.getJWTClaimsSet().getJWTID();
        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        RefreshToken refreshToken =
                RefreshToken.builder().id(jit).finalize(expiryTime).build();

        refreshTokenRepository.save(refreshToken);

        var email = signedJWT.getJWTClaimsSet().getSubject();

        Account account = accountRepository.findByEmailAndIsDeletedFalse(email)
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));

        String newToken = jwtUtil.generateAccessToken(account);
        AccountResponse accountResponse = accountMapper.toResponse(account);

        return AuthenticationResponse.builder()
                .accessToken(newToken)
                .accountResponse(accountResponse)
                .build();
    }

    @Override
    @Transactional
    public AuthenticationResponse register(RegisterRequest request) {
        if (accountRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.ACCOUNT_EXISTED);
        }

        Account account = accountMapper.toEntityFromRegister(request);
        account.setRole(Role.MEMBER);
        account.setPassword(passwordEncoder.encode(request.getPassword()));
        account.setSetting(Setting.getDefaultSetting(account));
        account.setStatus(AccountStatus.ONLINE);

        String accessToken = jwtUtil.generateAccessToken(account);
        AccountResponse accountResponse = accountMapper.toResponse(accountRepository.save(account));

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .accountResponse(accountResponse)
                .build();
    }

    @Override
    public IntrospectResponse introspect(TokenRequest request) throws JOSEException, ParseException {
        var token = request.getToken();
        boolean isValid = true;

        try {
            jwtUtil.verifyToken(token, false);
        } catch (AppException e) {
            isValid = false;
        }

        return IntrospectResponse.builder().valid(isValid).build();
    }

    @Override
    public void sendResetPasswordEmail(String email) {
        Account account = accountRepository.findByEmailAndIsDeletedFalse(email)
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));

        String token = jwtUtil.generateResetEmailToken(account);
        String resetLink = FRONTEND_DOMAIN + "/reset-password?token=" + token;

        try {
            mailService.sendResetPasswordEmail(email, resetLink, account.getEmail());
        } catch (MessagingException e) {
            log.error("Failed to send reset password email to {}", email, e);
            throw new AppException(ErrorCode.EMAIL_SEND_FAILED);
        }
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        String emailFromToken;
        String jwtId;

        try {
            JWSObject jwsObject = JWSObject.parse(request.getToken());
            JWTClaimsSet jwtClaimsSet = JWTClaimsSet.parse(jwsObject.getPayload().toJSONObject());
            if (!jwtClaimsSet.getClaim("type").equals("reset")) {
                throw new AppException(ErrorCode.INVALID_RESET_TOKEN);
            }
            if (jwtClaimsSet.getExpirationTime().before(new Date()) || refreshTokenRepository.existsById(jwtClaimsSet.getJWTID())) {
                throw new AppException(ErrorCode.TOKEN_EXPIRED);
            }
            emailFromToken = jwtClaimsSet.getSubject();
            jwtId = jwtClaimsSet.getJWTID();
        } catch (Exception e) {
            throw new AppException(ErrorCode.INVALID_RESET_TOKEN);
        }

        Account account = accountRepository.findByEmailAndIsDeletedFalse(emailFromToken).orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

        account.setPassword(passwordEncoder.encode(request.getNewPassword()));
        accountRepository.save(account);

        if (jwtId != null) {
            RefreshToken refreshToken = RefreshToken.builder()
                    .id(jwtId)
                    .expiryTime(null)
                    .build();

            refreshTokenRepository.save(refreshToken);
        }
    }

    @Override
    @Transactional
    public void logout() throws ParseException, JOSEException {
        String token = authUtil.getCurrentToken()
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));

        SignedJWT signedJWT = jwtUtil.verifyToken(token, true);

        String jwtId = signedJWT.getJWTClaimsSet().getJWTID();
        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        if (jwtId != null) {
            RefreshToken refreshToken = RefreshToken.builder()
                    .id(jwtId)
                    .expiryTime(expiryTime)
                    .build();
            refreshTokenRepository.save(refreshToken);
        }
    }
    
}
