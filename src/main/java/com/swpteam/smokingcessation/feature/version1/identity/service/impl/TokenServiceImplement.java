package com.swpteam.smokingcessation.feature.version1.identity.service.impl;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;
import com.swpteam.smokingcessation.constant.ErrorCode;
import com.swpteam.smokingcessation.domain.entity.Account;
import com.swpteam.smokingcessation.domain.entity.Token;
import com.swpteam.smokingcessation.domain.enums.TokenType;
import com.swpteam.smokingcessation.exception.AppException;
import com.swpteam.smokingcessation.repository.jpa.TokenRepository;
import com.swpteam.smokingcessation.feature.version1.identity.service.ITokenService;
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

    final TokenRepository tokenRepository;

    @Value("${jwt.access-token.duration}")
    long ACCESS_TOKEN_DURATION;

    @Value("${jwt.refresh-token.duration}")
    long REFRESH_TOKEN_DURATION;

    @Value("${jwt.password-reset-token.duration}")
    long PASSWORD_RESET_TOKEN_DURATION;

    @Value("${jwt.email-verification-token.duration}")
    long EMAIL_VERIFY_TOKEN_DURATION;

    @Value("${jwt.access-token.signer-key}")
    String ACCESS_TOKEN_SIGNER_KEY;

    @Value("${jwt.refresh-token.signer-key}")
    String REFRESH_TOKEN_SIGNER_KEY;

    @Value("${jwt.password-reset-token.signer-key}")
    String PASSWORD_RESET_TOKEN_SIGNER_KEY;

    @Value("${jwt.email-verification-token.signer-key}")
    String EMAIL_VERIFY_TOKEN_SIGNER_KEY;

    JWSSigner accessTokenSigner;
    JWSSigner refreshTokenSigner;
    JWSSigner passwordResetTokenSigner;
    JWSSigner verificationEmailTokenSigner;

    JWSVerifier accessTokenVerifier;
    JWSVerifier refreshTokenVerifier;
    JWSVerifier passwordResetTokenVerifier;
    JWSVerifier verificationEmailTokenVerifier;

    @PostConstruct
    public void init() {
        try {
            accessTokenSigner = new MACSigner(ACCESS_TOKEN_SIGNER_KEY);
            refreshTokenSigner = new MACSigner(REFRESH_TOKEN_SIGNER_KEY);
            passwordResetTokenSigner = new MACSigner(PASSWORD_RESET_TOKEN_SIGNER_KEY);
            verificationEmailTokenSigner = new MACSigner(EMAIL_VERIFY_TOKEN_SIGNER_KEY);

            accessTokenVerifier = new MACVerifier(ACCESS_TOKEN_SIGNER_KEY);
            refreshTokenVerifier = new MACVerifier(REFRESH_TOKEN_SIGNER_KEY);
            passwordResetTokenVerifier = new MACVerifier(PASSWORD_RESET_TOKEN_SIGNER_KEY);
            verificationEmailTokenVerifier = new MACVerifier(EMAIL_VERIFY_TOKEN_SIGNER_KEY);

        } catch (KeyLengthException e) {
            throw new IllegalArgumentException("Invalid secret key length", e);
        } catch (JOSEException e) {
            throw new RuntimeException("Failed to initialize JWT signer/verifier", e);
        }
    }


    // Generators

    @Override
    public String generateAccessToken(Account account) {
        return JwtUtil.generateToken(
                account,
                accessTokenSigner,
                ACCESS_TOKEN_DURATION,
                TokenType.ACCESS
        ).serialize();
    }

    @Override
    public String generateRefreshToken(Account account) {
        return generateAndSaveToken(account, TokenType.REFRESH);
    }

    @Override
    public String generateResetPasswordToken(Account account) {
        return generateAndSaveToken(account, TokenType.PASSWORD);
    }

    @Override
    public String generateVerificationEmailToken(Account account) {
        return generateAndSaveToken(account, TokenType.VERIFY);
    }

    @Transactional
    private String generateAndSaveToken(Account account, TokenType tokenType) {
        tokenRepository.deleteByAccountIdAndTokenType(account.getId(), tokenType);

        TokenSignerInfo tokenSignerInfo = getTokenSignerInfo(tokenType);

        SignedJWT token = JwtUtil.generateToken(
                account,
                tokenSignerInfo.signer(),
                tokenSignerInfo.expiration(),
                tokenType
        );

        tokenRepository.save(Token.builder()
                .id(JwtUtil.getJti(token))
                .account(account)
                .tokenType(tokenType)
                .expiryTime(JwtUtil.getExpiration(token))
                .build());

        return token.serialize();
    }

    private record TokenSignerInfo(JWSSigner signer, long expiration) {}

    private TokenSignerInfo getTokenSignerInfo(TokenType tokenType) {
        return switch(tokenType) {
            case TokenType.REFRESH
                    -> new TokenSignerInfo(refreshTokenSigner, REFRESH_TOKEN_DURATION);

            case TokenType.PASSWORD
                    -> new TokenSignerInfo(passwordResetTokenSigner, PASSWORD_RESET_TOKEN_DURATION);

            case TokenType.VERIFY
                    -> new TokenSignerInfo(verificationEmailTokenSigner, EMAIL_VERIFY_TOKEN_DURATION);

            default -> throw new AppException(ErrorCode.TOKEN_NOT_SAVABLE);
        };
    }


    // Verifiers

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
    public SignedJWT verifyVerificationEmailToken(String token) {
        return JwtUtil.verifyToken(token, verificationEmailTokenVerifier);
    }


    // Finder

    @Override
    public Token findTokenByJtiOrThrowError(String jti) {
        Token token = tokenRepository.findById(jti)
                .orElseThrow(() -> new AppException(ErrorCode.TOKEN_NOT_FOUND));

        if (token.getAccount().isDeleted()) {
            tokenRepository.deleteById(jti);
            throw new AppException(ErrorCode.ACCOUNT_NOT_FOUND);
        }

        return token;
    }

    @Override
    public String getAccountIdByRefreshToken(String token) {
        return JwtUtil.extractAccountIdWithVerification(token, refreshTokenVerifier);
    }

    @Override
    public String getAccountIdByResetPasswordToken(String token) {
        return JwtUtil.extractAccountIdWithVerification(token, passwordResetTokenVerifier);
    }

    @Override
    public String getAccountIdByEmailVerificationToken(String token) {
        return JwtUtil.extractAccountIdWithVerification(token, verificationEmailTokenVerifier);
    }

    // Revokes

    @Override
    public void revokeTokenByJti(String jti) {
        findTokenByJtiOrThrowError(jti);
        tokenRepository.deleteById(jti);
    }

    @Override
    public void revokeRefreshTokenByToken(String token) {
        String jti = JwtUtil.extractJtiWithVerification(token, refreshTokenVerifier);
        revokeTokenByJti(jti);
    }

}
