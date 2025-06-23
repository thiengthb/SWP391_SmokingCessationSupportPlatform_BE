package com.swpteam.smokingcessation.service.interfaces.identity;

import com.nimbusds.jwt.SignedJWT;
import com.swpteam.smokingcessation.domain.entity.Account;
import com.swpteam.smokingcessation.domain.entity.RefreshToken;

public interface ITokenService {

    String generateAccessToken(Account account);

    String generateRefreshToken(Account account);

    String generateResetEmailToken(Account account);

    SignedJWT verifyAccessToken(String token);

    SignedJWT verifyRefreshToken(String token);

    SignedJWT verifyResetPasswordToken(String token);

    RefreshToken findRefreshTokenByJtiOrThrowError(String jti);

    RefreshToken findRefreshTokenByAccountIdOrThrowError(String accountId);

    void revokeRefreshTokenByJti(String jti);

    void revokeRefreshTokenByToken(String token);

    String getAccountIdByRefreshToken(String token);

}
