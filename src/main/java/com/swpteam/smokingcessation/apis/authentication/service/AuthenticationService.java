package com.swpteam.smokingcessation.apis.authentication.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.swpteam.smokingcessation.apis.account.dto.response.AccountResponse;
import com.swpteam.smokingcessation.apis.account.entity.Account;
import com.swpteam.smokingcessation.apis.account.entity.AccountStatus;
import com.swpteam.smokingcessation.apis.account.entity.Role;
import com.swpteam.smokingcessation.apis.account.mapper.AccountMapper;
import com.swpteam.smokingcessation.apis.account.repository.AccountRepository;
import com.swpteam.smokingcessation.apis.authentication.dto.request.AuthenticationRequest;
import com.swpteam.smokingcessation.apis.authentication.dto.request.GoogleTokenRequest;
import com.swpteam.smokingcessation.apis.authentication.dto.request.RefreshTokenRequest;
import com.swpteam.smokingcessation.apis.authentication.dto.request.RegisterRequest;
import com.swpteam.smokingcessation.apis.authentication.dto.response.AuthenticationResponse;
import com.swpteam.smokingcessation.apis.authentication.dto.response.GoogleTokenResponse;
import com.swpteam.smokingcessation.exception.AppException;
import com.swpteam.smokingcessation.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Slf4j
@RequiredArgsConstructor
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {

    AccountRepository accountRepository;
    AccountMapper accountMapper;
    WebClient webClient = WebClient.create();

    @NonFinal
    @Value("${spring.jwt.signerkey}")
    protected String SIGNER_KEY;
    @NonFinal
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    protected String CLIENT_ID;
    @NonFinal
    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    protected String CLIENT_SECRET;
    @NonFinal
    @Value("${spring.security.oauth2.client.registration.google.redirect-uri:http://localhost:8080/oauth2/callback}")
    protected String REDIRECT_URI;

    public com.swpteam.smokingcessation.apis.authentication.dto.response.GoogleTokenResponse getGoogleToken(GoogleTokenRequest request) {
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

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var account = accountRepository.findByEmail(request.getEmail()).orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_EXISTED));

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

        boolean authenticated = passwordEncoder.matches(request.getPassword(), account.getPassword());

        if (!authenticated) {
            throw new AppException(ErrorCode.ACCOUNT_NOT_EXISTED);
        }

        var accessToken = generateToken(request.getEmail());
        var refreshToken = generateRefreshToken(request.getEmail());

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .authenticated(true)
                .build();
    }

    private String generateToken(String email) {
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(email)
                .issuer("swpteam")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()
                ))
                .claim("customClaim", "claim")
                .build();


        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(jwsHeader, payload);
        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot create token", e);
            throw new RuntimeException(e);
        }

    }

    private String generateRefreshToken(String email) {
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(email)
                .issuer("swpteam")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(30, ChronoUnit.DAYS).toEpochMilli() // 30 days validity
                ))
                .claim("type", "refresh")
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(jwsHeader, payload);
        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot create refresh token", e);
            throw new RuntimeException(e);
        }
    }

    //Using refresh token to issue a new token
    public AuthenticationResponse refreshToken(RefreshTokenRequest request) throws ParseException, JOSEException {
        String refreshToken = request.getRefreshToken();
        SignedJWT signedJWT;

        signedJWT = SignedJWT.parse(refreshToken);
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

        if (!signedJWT.verify(verifier)) {
            throw new SecurityException("Invalid JWT signature");
        }

        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        if (expiryTime.before(new Date())) {
            throw new AppException(ErrorCode.TOKEN_EXPIRED); // or a custom error
        }

        String email = signedJWT.getJWTClaimsSet().getSubject();

        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_EXISTED));

        String newToken = generateToken(email);

        return AuthenticationResponse.builder()
                .accessToken(newToken)
                .refreshToken(refreshToken)
                .authenticated(true)
                .build();
    }

    public AccountResponse register(RegisterRequest request) {
        if (accountRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.ACCOUNT_EXISTED);
        }

        Account account = accountMapper.toAccount(
                com.swpteam.smokingcessation.apis.account.dto.request.AccountCreateRequest.builder()
                        .email(request.getEmail())
                        .password(request.getPassword())
                        .phoneNumber(request.getPhoneNumber())
                        .role(Role.MEMBER)
                        .status(AccountStatus.ACTIVE)
                        .isDeleted(false)
                        .build()
        );

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        account.setPassword(passwordEncoder.encode(request.getPassword()));
        return accountMapper.toAccountResponse(accountRepository.save(account));
    }
}
