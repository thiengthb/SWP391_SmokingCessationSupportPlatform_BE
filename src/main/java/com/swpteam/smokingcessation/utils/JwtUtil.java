package com.swpteam.smokingcessation.utils;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.swpteam.smokingcessation.constant.ErrorCode;
import com.swpteam.smokingcessation.domain.entity.Account;
import com.swpteam.smokingcessation.exception.AppException;
import com.swpteam.smokingcessation.repository.RefreshTokenRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtUtil {

    RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.signer-key}")
    String SIGNER_KEY;

    @Value("${jwt.access-token-duration}")
    long ACCESS_TOKEN_DURATION;

    @Value("${jwt.refresh-token-duration}")
    long REFRESH_TOKEN_DURATION;

    @Value("${jwt.password-reset-token-duration}")
    long RESET_EMAIL_DURATION;


    public String generateAccessToken(Account account) {
        return generateToken(account, ACCESS_TOKEN_DURATION, buildScope(account), null);
    }

    public String generateRefreshToken(Account account) {
        return generateToken(account, REFRESH_TOKEN_DURATION, null, "refresh_token");
    }

    public String generateResetEmailToken(Account account) {
        return generateToken(account, REFRESH_TOKEN_DURATION, null, "reset_email_token");
    }

    @Transactional
    public SignedJWT verifyToken(String token, boolean isRefresh) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);

            boolean verified = signedJWT.verify(new MACVerifier(SIGNER_KEY.getBytes()));
            if (!verified)
                throw new AppException(ErrorCode.UNAUTHENTICATED);

            Date expirationTime = isRefresh ?
                    Date.from(signedJWT.getJWTClaimsSet().getIssueTime().toInstant()
                    .plus(REFRESH_TOKEN_DURATION, ChronoUnit.SECONDS))
                    :
                    signedJWT.getJWTClaimsSet().getExpirationTime();

            if (expirationTime.toInstant().isBefore(Instant.now())) {
                throw new AppException(ErrorCode.UNAUTHENTICATED);
            }

            String jti = signedJWT.getJWTClaimsSet().getJWTID();
            if (refreshTokenRepository.existsById(jti)) {
                throw new AppException(ErrorCode.USED_TOKEN);
            }

            return signedJWT;

        } catch (JOSEException | ParseException e) {
            log.error("Token verification failed", e);
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
    }

    private String buildScope(Account account) {
        StringJoiner stringJoiner = new StringJoiner(" ");

        if (account.getRole() != null) {
            stringJoiner.add("ROLE_" + account.getRole().name());
        }

        return stringJoiner.toString();
    }

    private String generateToken(Account account, long durationSeconds, String scope, String type) {
        try {
            JWTClaimsSet.Builder claimsBuilder = new JWTClaimsSet.Builder()
                    .jwtID(UUID.randomUUID().toString())
                    .subject(account.getEmail())
                    .issuer("swpteam")
                    .issueTime(new Date())
                    .expirationTime(Date.from(Instant.now().plus(durationSeconds, ChronoUnit.SECONDS)));

            if (scope != null) {
                claimsBuilder.claim("scope", scope);
            }
            if (type != null) {
                claimsBuilder.claim("token_type", type);
            }

            JWTClaimsSet claims = claimsBuilder.build();

            SignedJWT signedJWT = new SignedJWT(
                    new JWSHeader.Builder(JWSAlgorithm.HS512).type(JOSEObjectType.JWT).build(),
                    claims
            );

            signedJWT.sign(new MACSigner(SIGNER_KEY.getBytes()));

            return signedJWT.serialize();
        } catch (JOSEException e) {
            log.error("Failed to generate token", e);
            throw new RuntimeException("Token generation error", e);
        }
    }

}
