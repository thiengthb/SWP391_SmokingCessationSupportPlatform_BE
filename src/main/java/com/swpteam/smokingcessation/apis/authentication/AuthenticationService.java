package com.swpteam.smokingcessation.apis.authentication;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.swpteam.smokingcessation.apis.account.Account;
import com.swpteam.smokingcessation.apis.account.AccountMapper;
import com.swpteam.smokingcessation.apis.account.AccountRepository;
import com.swpteam.smokingcessation.apis.account.dto.AccountCreateRequest;
import com.swpteam.smokingcessation.apis.account.dto.AccountResponse;
import com.swpteam.smokingcessation.apis.account.enums.AccountStatus;
import com.swpteam.smokingcessation.apis.account.enums.Role;
import com.swpteam.smokingcessation.apis.authentication.dto.request.*;
import com.swpteam.smokingcessation.apis.authentication.dto.response.AuthenticationResponse;
import com.swpteam.smokingcessation.apis.authentication.dto.response.GoogleTokenResponse;
import com.swpteam.smokingcessation.apis.authentication.dto.response.IntrospectResponse;
import com.swpteam.smokingcessation.apis.mail.MailService;
import com.swpteam.smokingcessation.constants.ErrorCode;
import com.swpteam.smokingcessation.exception.AppException;
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
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {

    AccountRepository accountRepository;
    AccountMapper accountMapper;
    InvalidatedTokenRepository invalidatedTokenRepository;
    MailService mailService;
    WebClient webClient = WebClient.create();

    @NonFinal
    @Value("${jwt.signer-key}")
    protected String SIGNER_KEY;

    @NonFinal
    @Value("${jwt.valid-duration}")
    protected long VALID_DURATION;

    @NonFinal
    @Value("${jwt.refreshable-duration}")
    protected long REFRESHABLE_DURATION;

    @NonFinal
    @Value("${jwt.reset-duration}")
    protected long RESET_DURATION;

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

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var account = accountRepository.findByEmail(request.getEmail()).orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_EXISTED));

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

        boolean authenticated = passwordEncoder.matches(request.getPassword(), account.getPassword());

        if (!authenticated) {
            throw new AppException(ErrorCode.WRONG_PASSWORD);
        }

        var accessToken = generateToken(account);
        var refreshToken = generateRefreshToken(account);

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .authenticated(true)
                .build();
    }

    private String generateToken(Account account) {
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(account.getEmail())
                .issuer("swpteam")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli()
                ))
                .claim("scope", buildScope(account))
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


    private String generateRefreshToken(Account account) {
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(account.getEmail())
                .issuer("swpteam")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS).toEpochMilli() // 30 days validity
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

    private String generateResetToken(Account account) {
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .jwtID(UUID.randomUUID().toString())//unique ID to invalidate later on
                .subject(account.getEmail())
                .issuer("swpteam")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(RESET_DURATION, ChronoUnit.SECONDS).toEpochMilli() // 30 days validity
                ))
                .claim("type", "reset")
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(jwsHeader, payload);
        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot create reset token", e);
            throw new RuntimeException(e);
        }
    }

    private String buildScope(Account account) {
        StringJoiner stringJoiner = new StringJoiner(" ");

        if (account.getRole() != null) {
            stringJoiner.add("ROLE_" + account.getRole().name());
        }

        return stringJoiner.toString();
    }

    //Using refresh token to issue a new token
    public AuthenticationResponse refreshToken(RefreshTokenRequest request) throws ParseException, JOSEException {
        String refreshToken = request.getRefreshToken();
        SignedJWT signedJWT;

        signedJWT = SignedJWT.parse(refreshToken);
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

        if (!signedJWT.verify(verifier)) {
            throw new AppException(ErrorCode.INVALID_SIGNATURE);
        }

        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        if (expiryTime.before(new Date())) {
            throw new AppException(ErrorCode.TOKEN_EXPIRED);
        }

        String email = signedJWT.getJWTClaimsSet().getSubject();

        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_EXISTED));

        String newToken = generateToken(account);

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

        Account account = accountMapper.toAccount(AccountCreateRequest.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .phoneNumber(request.getPhoneNumber())
                .role(Role.MEMBER)
                .status(AccountStatus.ACTIVE)
                .build()
        );

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

        account.setPassword(passwordEncoder.encode(request.getPassword()));
        return accountMapper.toAccountResponse(accountRepository.save(account));
    }

    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
        var token = request.getToken();
        boolean isValid = true;

        try {
            verifyToken(token, false);
        } catch (AppException e) {
            isValid = false;
        }

        return IntrospectResponse.builder().valid(isValid).build();
    }

    private SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        if (expiryTime == null) {
            throw new AppException(ErrorCode.INVALID_SIGNATURE);
        }

        var verified = signedJWT.verify(verifier);

        if (!(verified && expiryTime.after(new Date()))) throw new AppException(ErrorCode.UNAUTHENTICATED);

        if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        return signedJWT;
    }

    public void sendResetPasswordEmail(String email) {
        Account account = accountRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_EXISTED));

        //generate reset token
        String token = generateResetToken(account);
        String resetLink = FRONTEND_DOMAIN + "/reset-password?token=" + token;

        try {
            mailService.sendResetPasswordEmail(email, resetLink, account.getEmail());
        } catch (MessagingException e) {
            log.error("Failed to send reset password email to {}", email, e);
            throw new AppException(ErrorCode.EMAIL_SEND_FAILED);
        }
    }

    public void resetPassword(ResetPasswordRequest request) {
        String emailFromToken;
        String jwtId;

        try {
            JWSObject jwsObject = JWSObject.parse(request.getToken());
            JWTClaimsSet jwtClaimsSet = JWTClaimsSet.parse(jwsObject.getPayload().toJSONObject());
            if (!jwtClaimsSet.getClaim("type").equals("reset")) {
                throw new AppException(ErrorCode.INVALID_RESET_TOKEN);
            }
            if (jwtClaimsSet.getExpirationTime().before(new Date()) || invalidatedTokenRepository.existsById(jwtClaimsSet.getJWTID())) {
                throw new AppException(ErrorCode.TOKEN_EXPIRED);
            }
            emailFromToken = jwtClaimsSet.getSubject();
            jwtId = jwtClaimsSet.getJWTID();
        } catch (Exception e) {
            throw new AppException(ErrorCode.INVALID_RESET_TOKEN);
        }

        Account account = accountRepository.findByEmail(emailFromToken).orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_EXISTED));

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

        account.setPassword(passwordEncoder.encode(request.getNewPassword()));
        accountRepository.save(account);

        if (jwtId != null) {
            InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                    .id(jwtId)
                    .expiryTime(null)
                    .build();

            invalidatedTokenRepository.save(invalidatedToken);
        }
    }
}
