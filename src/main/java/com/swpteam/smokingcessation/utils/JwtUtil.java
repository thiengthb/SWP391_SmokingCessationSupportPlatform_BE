package com.swpteam.smokingcessation.utils;

import com.nimbusds.jose.*;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.swpteam.smokingcessation.constant.ErrorCode;
import com.swpteam.smokingcessation.domain.entity.Account;
import com.swpteam.smokingcessation.domain.enums.TokenType;
import com.swpteam.smokingcessation.exception.AppException;
import lombok.experimental.UtilityClass;

import java.text.ParseException;
import java.time.*;
import java.util.Date;
import java.util.UUID;

@UtilityClass
public class JwtUtil {

    public SignedJWT generateToken(Account account, JWSSigner signer, long expirySeconds, TokenType tokenType) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiry = now.plusSeconds(expirySeconds);

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(account.getId())
                .issuer("swp-team")
                .issueTime(toDate(now))
                .expirationTime(toDate(expiry))
                .claim("token_type", tokenType.name().toUpperCase())
                .claim("role", account.getRole().name())
                .jwtID(UUID.randomUUID().toString())
                .build();

        try {
            SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
            signedJWT.sign(signer);
            return signedJWT;
        } catch (JOSEException e) {
            throw new AppException(ErrorCode.TOKEN_CREATION_FAILED);
        }
    }

    public SignedJWT verifyToken(String token, JWSVerifier verifier) {
        if (token == null || token.trim().isEmpty()) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }

        try {
            SignedJWT jwt = SignedJWT.parse(token);

            Object claim = jwt.getJWTClaimsSet().getClaim("token_type");
            if (!(claim instanceof String tokenTypeStr)) {
                throw new AppException(ErrorCode.INVALID_TOKEN);
            }

            try {
                TokenType.valueOf(tokenTypeStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new AppException(ErrorCode.INVALID_TOKEN);
            }

            if (!jwt.verify(verifier)) {
                throw new AppException(ErrorCode.INVALID_TOKEN);
            }

            LocalDateTime expiration = getExpiration(jwt);
            if (expiration.isBefore(LocalDateTime.now())) {
                throw new AppException(ErrorCode.TOKEN_EXPIRED);
            }

            return jwt;
        } catch (ParseException | JOSEException e) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }
    }

    public String extractJtiWithVerification(String token, JWSVerifier verifier) {
        try {
            SignedJWT jwt = SignedJWT.parse(token);
            if (!jwt.verify(verifier)) {
                throw new AppException(ErrorCode.INVALID_TOKEN);
            }
            return jwt.getJWTClaimsSet().getJWTID();
        } catch (ParseException | JOSEException e) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }
    }

    public String extractAccountIdWithVerification(String token, JWSVerifier verifier) {
        try {
            SignedJWT jwt = SignedJWT.parse(token);
            if (!jwt.verify(verifier)) {
                throw new AppException(ErrorCode.INVALID_TOKEN);
            }
            return jwt.getJWTClaimsSet().getSubject();
        } catch (ParseException | JOSEException e) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }
    }

    public String getSubject(SignedJWT jwt) {
        try {
            return jwt.getJWTClaimsSet().getSubject();
        } catch (ParseException e) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }
    }

    public String getJti(SignedJWT jwt) {
        try {
            return jwt.getJWTClaimsSet().getJWTID();
        } catch (ParseException e) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }
    }

    public LocalDateTime getExpiration(SignedJWT jwt) {
        try {
            Date expiration = jwt.getJWTClaimsSet().getExpirationTime();
            return toLocalDateTime(expiration);
        } catch (ParseException e) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }
    }

    public Object getClaim(SignedJWT jwt, String claimKey) {
        try {
            return jwt.getJWTClaimsSet().getClaim(claimKey);
        } catch (ParseException e) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }
    }

    // ✅ Helper methods
    private Date toDate(LocalDateTime ldt) {
        return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
    }

    private LocalDateTime toLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }
}
