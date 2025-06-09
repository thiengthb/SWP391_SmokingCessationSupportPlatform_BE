package com.swpteam.smokingcessation.security;

import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;
import com.swpteam.smokingcessation.apis.account.Account;
import com.swpteam.smokingcessation.apis.account.AccountRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AccountRepository accountRepository;

    @Value("${jwt.signer-key}")
    private String SIGNER_KEY;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                SignedJWT signedJWT = SignedJWT.parse(token);
                JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
                if (signedJWT.verify(verifier)) {
                    String email = signedJWT.getJWTClaimsSet().getSubject();
                    Account account = accountRepository.findByEmail(email).orElse(null);
                    if (account != null) {
                        String role = account.getRole().name();
                        User principal = new User(
                                account.getEmail(),
                                account.getPassword(),
                                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role)));
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            } catch (Exception e) {
                logger.warn("JWT authentication failed: {}");
            }
        }
        filterChain.doFilter(request, response);
    }
}