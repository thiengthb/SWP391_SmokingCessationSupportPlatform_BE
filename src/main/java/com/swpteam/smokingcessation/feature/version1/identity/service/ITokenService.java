package com.swpteam.smokingcessation.feature.version1.identity.service;

import com.nimbusds.jwt.SignedJWT;
import com.swpteam.smokingcessation.domain.entity.Account;
import com.swpteam.smokingcessation.domain.entity.Token;

public interface ITokenService {

    String generateAccessToken(Account account);

    String generateRefreshToken(Account account);

    String generateResetPasswordToken(Account account);

    String generateVerificationEmailToken(Account account);

    SignedJWT verifyAccessToken(String token);

    SignedJWT verifyRefreshToken(String token);

    SignedJWT verifyResetPasswordToken(String token);

    SignedJWT verifyVerificationEmailToken(String token);

    Token findTokenByJtiOrThrowError(String jti);

    String getAccountIdByRefreshToken(String token);

    String getAccountIdByResetPasswordToken(String token);

    String getAccountIdByEmailVerificationToken(String token);

    void revokeTokenByJti(String jti);

    void revokeRefreshTokenByToken(String token);

}
