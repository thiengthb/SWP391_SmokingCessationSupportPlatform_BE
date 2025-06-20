package com.swpteam.smokingcessation.service.impl.identity;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;
import com.swpteam.smokingcessation.constant.ErrorCode;
import com.swpteam.smokingcessation.domain.entity.Account;
import com.swpteam.smokingcessation.domain.entity.RefreshToken;
import com.swpteam.smokingcessation.exception.AppException;
import com.swpteam.smokingcessation.repository.RefreshTokenRepository;
import com.swpteam.smokingcessation.service.interfaces.identity.ITokenService;
import com.swpteam.smokingcessation.utils.JwtUtil;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TokenServiceImplement implements ITokenService {

    final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.access-token.duration}")
    long ACCESS_TOKEN_DURATION;

    @Value("${jwt.refresh-token.duration}")
    long REFRESH_TOKEN_DURATION;

    @Value("${jwt.password-reset-token.duration}")
    long RESET_EMAIL_DURATION;

    @Value("${jwt.access-token.signer-key}")
    String ACCESS_TOKEN_SIGNER_KEY;

    @Value("${jwt.refresh-token.signer-key}")
    String REFRESH_TOKEN_SIGNER_KEY;

    @Value("${jwt.password-reset-token.signer-key}")
    String PASSWORD_RESET_TOKEN_SIGNER_KEY;

    JWSSigner accessTokenSigner;
    JWSSigner refreshTokenSigner;
    JWSSigner passwordResetTokenSigner;

    JWSVerifier accessTokenVerifier;
    JWSVerifier refreshTokenVerifier;
    JWSVerifier passwordResetTokenVerifier;

    @PostConstruct
    public void init() {
        try {
            accessTokenSigner = new MACSigner(ACCESS_TOKEN_SIGNER_KEY);
            refreshTokenSigner = new MACSigner(REFRESH_TOKEN_SIGNER_KEY);
            passwordResetTokenSigner = new MACSigner(PASSWORD_RESET_TOKEN_SIGNER_KEY);

            accessTokenVerifier = new MACVerifier(ACCESS_TOKEN_SIGNER_KEY);
            refreshTokenVerifier = new MACVerifier(REFRESH_TOKEN_SIGNER_KEY);
            passwordResetTokenVerifier = new MACVerifier(PASSWORD_RESET_TOKEN_SIGNER_KEY);
        } catch (KeyLengthException e) {
            throw new IllegalArgumentException("Invalid secret key length", e);
        } catch (JOSEException e) {
            throw new RuntimeException("Failed to initialize JWT signer/verifier", e);
        }
    }

    @Override
    public String generateAccessToken(Account account) {
        return JwtUtil.generateToken(
                account,
                accessTokenSigner,
                ACCESS_TOKEN_DURATION,
                JwtUtil.TokenType.ACCESS
        ).serialize();
    }

    @Override
    @Transactional
    public String generateRefreshToken(Account account) {
        refreshTokenRepository.deleteByAccountId(account.getId());

        SignedJWT refreshToken = JwtUtil.generateToken(
                account,
                refreshTokenSigner,
                REFRESH_TOKEN_DURATION,
                JwtUtil.TokenType.REFRESH
        );

        refreshTokenRepository.save(RefreshToken.builder()
                .id(JwtUtil.getJti(refreshToken))
                .account(account)
                .expiryTime(JwtUtil.getExpiration(refreshToken))
                .build());

        return refreshToken.serialize();
    }

    @Override
    public String generateResetEmailToken(Account account) {
        return JwtUtil.generateToken(
                account,
                passwordResetTokenSigner,
                RESET_EMAIL_DURATION,
                JwtUtil.TokenType.PASSWORD
        ).serialize();
    }

    @Override
    public SignedJWT verifyAccessToken(String token) {
        return JwtUtil.verifyToken(token, accessTokenVerifier);
    }

    @Override
    public SignedJWT verifyRefreshToken(String token) {
        return JwtUtil.verifyToken(token, refreshTokenVerifier);
    }

    @Override
    public SignedJWT verifyResetPasswordToken(String token) {
        return JwtUtil.verifyToken(token, passwordResetTokenVerifier);
    }

    @Override
    public RefreshToken findRefreshTokenByJtiOrThrowError(String jti) {
        RefreshToken refreshToken = refreshTokenRepository.findById(jti)
                .orElseThrow(() -> new AppException(ErrorCode.TOKEN_NOT_FOUND));

        if (refreshToken.getAccount().isDeleted()) {
            throw new AppException(ErrorCode.ACCOUNT_DELETED);
        }

        return refreshToken;
    }

    @Override
    public RefreshToken findRefreshTokenByAccountIdOrThrowError(String accountId) {
        RefreshToken refreshToken = refreshTokenRepository.findByAccountId(accountId)
                .orElseThrow(() -> new AppException(ErrorCode.TOKEN_NOT_FOUND));

        if (refreshToken.getAccount().isDeleted()) {
            throw new AppException(ErrorCode.ACCOUNT_DELETED);
        }

        return refreshToken;
    }

    @Override
    public void revokeRefreshTokenByJti(String jti) {
        findRefreshTokenByJtiOrThrowError(jti);
        refreshTokenRepository.deleteById(jti);
    }

    @Override
    public void revokeRefreshTokenByToken(String token) {
        String jti = JwtUtil.extractJtiWithVerification(token, refreshTokenVerifier);
        revokeRefreshTokenByJti(jti);
    }

    @Override
    public String getAccountIdByRefreshToken(String token) {
        return JwtUtil.extractAccountIdWithVerification(token, refreshTokenVerifier);
    }

}
