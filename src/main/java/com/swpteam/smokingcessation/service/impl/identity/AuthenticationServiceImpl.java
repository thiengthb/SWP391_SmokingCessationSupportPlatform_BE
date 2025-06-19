package com.swpteam.smokingcessation.service.impl.identity;

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
import com.swpteam.smokingcessation.repository.RefreshTokenRepository;
import com.swpteam.smokingcessation.domain.entity.Setting;
import com.swpteam.smokingcessation.constant.ErrorCode;
import com.swpteam.smokingcessation.domain.entity.RefreshToken;
import com.swpteam.smokingcessation.exception.AppException;
import com.swpteam.smokingcessation.security.UserPrincipal;
import com.swpteam.smokingcessation.service.interfaces.identity.IAccountService;
import com.swpteam.smokingcessation.service.interfaces.identity.IAuthenticationService;
import com.swpteam.smokingcessation.service.interfaces.identity.ITokenService;
import com.swpteam.smokingcessation.utils.AuthUtilService;
import com.swpteam.smokingcessation.utils.JwtUtil;
import jakarta.mail.MessagingException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationServiceImpl implements IAuthenticationService {

    AccountMapper accountMapper;
    AccountRepository accountRepository;
    IAccountService accountService;
    RefreshTokenRepository refreshTokenRepository;

    WebClient webClient = WebClient.create();
    PasswordEncoder passwordEncoder;

    IMailService mailService;
    AuthUtilService authUtilService;
    ITokenService tokenService;

    @NonFinal
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    String CLIENT_ID;

    @NonFinal
    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    String CLIENT_SECRET;

    @NonFinal
    @Value("${spring.security.oauth2.client.registration.google.redirect-uri:http://localhost:8080/oauth2/callback}")
    String REDIRECT_URI;

    @NonFinal
    @Value("${app.frontend-domain}")
    String FRONTEND_DOMAIN;

    @Override
    public GoogleTokenResponse getGoogleToken(@NotNull GoogleTokenRequest request) {
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
    public AuthenticationResponse login(@NotNull AuthenticationRequest request) {
        Account account = accountService.findAccountByEmail(request.getEmail());

        boolean authenticated = passwordEncoder.matches(request.getPassword(), account.getPassword());
        if (!authenticated) {
            throw new AppException(ErrorCode.WRONG_PASSWORD);
        }

        AccountResponse accountResponse = accountMapper.toResponse(account);
        String accessToken = tokenService.generateAccessToken(account);
        String refreshToken = tokenService.generateRefreshToken(account);

        return AuthenticationResponse.builder()
                .accountResponse(accountResponse)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    @Transactional
    public AuthenticationResponse refreshingToken(String token) {
        SignedJWT jwt = tokenService.verifyRefreshToken(token);

        String jti = JwtUtil.getJti(jwt);
        RefreshToken currentRefreshToken = tokenService.findRefreshTokenByJti(jti);
        Account account = accountService.findAccountById(JwtUtil.getSubject(jwt));

        Date expiration = JwtUtil.getExpiration(jwt);
        long remainingMillis = expiration.getTime() - System.currentTimeMillis();

        String resultRefreshToken;
        if (remainingMillis < TimeUnit.DAYS.toMillis(1)) {
            tokenService.revokeRefreshToken(currentRefreshToken.getId());
            resultRefreshToken = tokenService.generateRefreshToken(account);
        } else {
            resultRefreshToken = token;
        }

        return AuthenticationResponse.builder()
                .accountResponse(accountMapper.toResponse(account))
                .accessToken(tokenService.generateAccessToken(account))
                .refreshToken(resultRefreshToken)
                .build();
    }

    @Override
    @Transactional
    public AuthenticationResponse register(@NotNull RegisterRequest request) {
        if (accountRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.ACCOUNT_EXISTED);
        }

        Account account = accountMapper.toEntityFromRegister(request);
        account.setPassword(passwordEncoder.encode(request.getPassword()));
        account.setRole(Role.MEMBER);
        account.setStatus(AccountStatus.ONLINE);
        account.setSetting(Setting.getDefaultSetting(account));

        account = accountRepository.save(account);

        return AuthenticationResponse.builder()
                .accountResponse(accountMapper.toResponse(account))
                .accessToken(tokenService.generateAccessToken(account))
                .refreshToken(tokenService.generateRefreshToken(account))
                .build();
    }

    @Override
    public Authentication authenticate(String accessToken) {
        SignedJWT jwt = tokenService.verifyAccessToken(accessToken);
        Account account = accountService.findAccountById(JwtUtil.getSubject(jwt));

        UserPrincipal principal = UserPrincipal.builder().account(account).build();
        return new UsernamePasswordAuthenticationToken(
                principal,
                null,
                principal.getAuthorities()
        );
    }

    @Override
    public void sendResetPasswordEmail(String email) {
        Account account = accountService.findAccountByEmail(email);

        String token = tokenService.generateResetEmailToken(account);
        String link = FRONTEND_DOMAIN + "/reset-password?token=" + token;

        try {
            mailService.sendResetPasswordEmail(email, link, account.getEmail());
        } catch (MessagingException e) {
            log.error("Send email failed", e);
            throw new AppException(ErrorCode.EMAIL_SEND_FAILED);
        }
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        SignedJWT jwt = tokenService.verifyResetPasswordToken(request.getToken());

        String accountId = JwtUtil.getSubject(jwt);
        Account account = accountService.findAccountById(accountId);
        account.setPassword(passwordEncoder.encode(request.getNewPassword()));
        accountRepository.save(account);
    }

    @Override
    @Transactional
    public void logout() {
        Account account = authUtilService.getCurrentAccountOrThrow();

        RefreshToken refreshToken = tokenService.findRefreshTokenByAccountId(account.getId());

        tokenService.revokeRefreshToken(refreshToken.getId());
    }

}
