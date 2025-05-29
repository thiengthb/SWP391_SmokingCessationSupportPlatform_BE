package com.swpteam.smoking_cessation.apis.authentication.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.swpteam.smoking_cessation.apis.authentication.dto.request.AuthenticationRequest;
import com.swpteam.smoking_cessation.apis.authentication.dto.request.GoogleTokenRequest;
import com.swpteam.smoking_cessation.apis.authentication.dto.request.IntrospectRequest;
import com.swpteam.smoking_cessation.apis.authentication.dto.response.AuthenticationResponse;
import com.swpteam.smoking_cessation.apis.authentication.dto.response.GoogleTokenResponse;
import com.swpteam.smoking_cessation.apis.authentication.dto.response.IntrospectResponse;
import com.swpteam.smoking_cessation.exception.AppException;
import com.swpteam.smoking_cessation.exception.ErrorCode;
import com.swpteam.smoking_cessation.apis.authentication.repository.AccountRepository;
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

    private final WebClient webClient = WebClient.create();
    public GoogleTokenResponse getGoogleToken(GoogleTokenRequest request){
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
        var account = accountRepository.findByUsername(request.getUsername()).orElseThrow(() -> new AppException(ErrorCode.USER_NOTEXIST));

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

        boolean authenticated = passwordEncoder.matches(request.getPassword(), account.getPassword());

        if (!authenticated) {
            throw new AppException(ErrorCode.USER_NOTEXIST);
        }

        var token = generateToken(request.getUsername());

        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .build();
    }
    private String generateToken(String username){
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(username)
                .issuer("swpteam")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()
                ))
                .claim("customClaim", "claim")
                .build();


        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(jwsHeader, payload);
        try{
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        }catch(JOSEException e){
            log.error("Cannot create token", e);
            throw new RuntimeException(e);
        }

    }

    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
        var token = request.getToken();
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);

        return IntrospectResponse.builder()
                .valid(verified && expiryTime.after(new Date()))
                .build();
    }
}
