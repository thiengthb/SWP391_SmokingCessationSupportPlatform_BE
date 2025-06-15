package com.swpteam.smokingcessation.utils;

import com.swpteam.smokingcessation.domain.entity.Account;
import com.swpteam.smokingcessation.repository.AccountRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AccountUtilService {

    AccountRepository accountRepository;

    public Optional<String> getCurrentEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            return Optional.ofNullable(jwt.getClaimAsString("sub"));
        }
        return Optional.empty();
    }

    public Optional<String> getCurrentToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            return Optional.ofNullable(jwt.getTokenValue());
        }
        return Optional.empty();
    }

    public Optional<Account> getCurrentAccount() {
        return getCurrentEmail()
                .flatMap(accountRepository::findByEmailAndIsDeletedFalse);
    }

    public Optional<String> getIdCurrentAccount() {
        return getCurrentEmail()
                .flatMap(accountRepository::findByEmailAndIsDeletedFalse)
                .map(Account::getId);
    }
}
