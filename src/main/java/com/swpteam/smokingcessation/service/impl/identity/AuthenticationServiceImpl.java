package com.swpteam.smokingcessation.service.impl.identity;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.nimbusds.jwt.SignedJWT;
import com.swpteam.smokingcessation.domain.dto.auth.request.*;
import com.swpteam.smokingcessation.domain.entity.Account;
import com.swpteam.smokingcessation.domain.entity.Score;
import com.swpteam.smokingcessation.domain.enums.AuthProvider;
import com.swpteam.smokingcessation.domain.mapper.AccountMapper;
import com.swpteam.smokingcessation.integration.google.GoogleTokenVerifier;
import com.swpteam.smokingcessation.integration.mail.IMailService;
import com.swpteam.smokingcessation.repository.AccountRepository;
import com.swpteam.smokingcessation.domain.dto.account.AccountResponse;
import com.swpteam.smokingcessation.domain.enums.AccountStatus;
import com.swpteam.smokingcessation.domain.enums.Role;
import com.swpteam.smokingcessation.domain.dto.auth.response.AuthenticationResponse;
import com.swpteam.smokingcessation.domain.entity.Setting;
import com.swpteam.smokingcessation.constant.ErrorCode;
import com.swpteam.smokingcessation.domain.entity.Token;
import com.swpteam.smokingcessation.exception.AppException;
import com.swpteam.smokingcessation.repository.TokenRepository;
import com.swpteam.smokingcessation.security.UserPrincipal;
import com.swpteam.smokingcessation.service.interfaces.identity.IAccountService;
import com.swpteam.smokingcessation.service.interfaces.identity.IAuthenticationService;
import com.swpteam.smokingcessation.service.interfaces.identity.ITokenService;
import com.swpteam.smokingcessation.service.interfaces.profile.IGoalProgressService;
import com.swpteam.smokingcessation.utils.JwtUtil;
import com.swpteam.smokingcessation.utils.RandomUtil;
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

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationServiceImpl implements IAuthenticationService {

    AccountMapper accountMapper;
    AccountRepository accountRepository;
    IAccountService accountService;
    PasswordEncoder passwordEncoder;
    IMailService mailService;
    ITokenService tokenService;
    GoogleTokenVerifier googleTokenVerifier;
    TokenRepository tokenRepository;
    IGoalProgressService goalProgressService;

    @NonFinal
    @Value("${app.frontend-domain}")
    String FRONTEND_DOMAIN;

    @NonFinal
    @Value("${app.backend-domain}")
    String backendUrl;

    @Override
    public AuthenticationResponse googleLogin(GoogleLoginRequest request) {
        GoogleIdToken.Payload payload = googleTokenVerifier.verify(request.idToken());

        Account account = accountService.createAccountByGoogle(payload);
        String accessToken = tokenService.generateAccessToken(account);
        String refreshToken = tokenService.generateRefreshToken(account);

        AccountResponse accountResponse = accountMapper.toResponse(account);

        return AuthenticationResponse.builder()
                .accountResponse(accountResponse)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public AuthenticationResponse login(@NotNull AuthenticationRequest request) {
        Account account = accountService.findAccountByEmailOrThrowError(request.email());

        boolean authenticated = passwordEncoder.matches(request.password(), account.getPassword());
        if (!authenticated) {
            throw new AppException(ErrorCode.WRONG_PASSWORD);
        }

        AccountResponse accountResponse = accountMapper.toResponse(account);
        String accessToken = tokenService.generateAccessToken(account);
        String refreshToken = tokenService.generateRefreshToken(account);

        accountService.updateStatus(account.getId(), AccountStatus.ONLINE);

        return AuthenticationResponse.builder()
                .accountResponse(accountResponse)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public AuthenticationResponse refreshingToken(String token) {
        SignedJWT jwt = tokenService.verifyRefreshToken(token);

        String jti = JwtUtil.getJti(jwt);
        Token currentToken = tokenService.findTokenByJtiOrThrowError(jti);
        Account account = accountService.findAccountByIdOrThrowError(JwtUtil.getSubject(jwt));

        LocalDateTime expiration = JwtUtil.getExpiration(jwt);
        long remainingMillis = Duration.between(LocalDateTime.now(), expiration).toMillis();

        String resultRefreshToken;
        if (remainingMillis < TimeUnit.DAYS.toMillis(1)) {
            tokenService.revokeTokenByJti(currentToken.getId());
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
    public void register(@NotNull RegisterRequest request) {
        accountService.checkExistByEmailOrThrowError(request.email());

        if (!request.password().equals(request.confirmPassword())) {
            throw new AppException(ErrorCode.PASSWORD_MISMATCH);
        }

        Account account = Account.builder()
                .email(request.email())
                .username(RandomUtil.generateRandomUsername())
                .password(passwordEncoder.encode(request.password()))
                .provider(AuthProvider.LOCAL)
                .status(AccountStatus.INACTIVE)
                .role(Role.MEMBER)
                .build();
        account.setSetting(Setting.getDefaultSetting(account));
        account.setScore(Score.getDefaultScore(account));

        accountRepository.save(account);

        goalProgressService.ensureGlobalProgressForNewAccount(account);

        sendEmailVerification(account.getEmail());
    }

    @Override
    public Authentication authenticate(String accessToken) {
        SignedJWT jwt = tokenService.verifyAccessToken(accessToken);
        Account account = accountService.findAccountByIdOrThrowError(JwtUtil.getSubject(jwt));

        UserPrincipal principal = UserPrincipal.builder().account(account).build();
        return new UsernamePasswordAuthenticationToken(
                principal,
                null,
                principal.getAuthorities()
        );
    }

    @Override
    public void sendResetPasswordEmail(String email) {
        Account account = accountService.findAccountByEmailOrThrowError(email);

        String token = tokenService.generateResetPasswordToken(account);
        String link = FRONTEND_DOMAIN + "/reset-password?token=" + token;

        mailService.sendResetPasswordEmail(email, link, account.getEmail());
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        SignedJWT jwt = tokenService.verifyResetPasswordToken(request.token());

        String accountId = JwtUtil.getSubject(jwt);

        accountService.changePassword(accountId, request.newPassword());
    }

    @Override
    public void sendEmailVerification(String email) {
        Account account = accountService.findAccountByEmailOrThrowError(email);

        if (account.getStatus() != AccountStatus.INACTIVE) {
            throw new AppException(ErrorCode.ACCOUNT_ACTIVATED);
        }

        String code = tokenService.generateVerificationEmailToken(account);

        String verificationLink = String.format("%s/api/v1/auth/verify?token=%s", backendUrl, code);

        mailService.sendVerificationEmail(account.getEmail(), account.getUsername(), verificationLink);
    }

    @Override
    public boolean verifyToActivateAccount(String token) {
        SignedJWT jwt;
        try {
            jwt = tokenService.verifyVerificationEmailToken(token);
        } catch (AppException exception) {
            return false;
        }

        String accountId = JwtUtil.getSubject(jwt);
        String jti = JwtUtil.getJti(jwt);

        Optional<Token> tokenOpt = tokenRepository.findById(jti);
        if (tokenOpt.isEmpty() || tokenOpt.get().getExpiryTime().isBefore(LocalDateTime.now())) {
            return false;
        }

        accountService.verifyAccount(accountId);
        tokenService.revokeTokenByJti(jti);
        return true;
    }

    @Override
    public void logout(String refreshToken) {
        tokenService.revokeRefreshTokenByToken(refreshToken);

        String accountId = tokenService.getAccountIdByRefreshToken(refreshToken);

        accountService.updateStatus(accountId, AccountStatus.OFFLINE);
    }

}
