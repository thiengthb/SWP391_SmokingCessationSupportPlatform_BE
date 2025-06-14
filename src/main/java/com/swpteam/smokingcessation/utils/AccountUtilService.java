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

    public String getCurrentEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Jwt) {
            return ((Jwt) authentication.getPrincipal()).getClaimAsString("sub");
        }
        return null;
    }

    public String getCurrentToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Jwt) {
            return ((Jwt) authentication.getPrincipal()).getTokenValue();
        }
        return null;
    }

    public Account getCurrentAccount() {
        String email = getCurrentEmail();
        if (email != null) {
            Optional<Account> account = accountRepository.findByEmailAndIsDeletedFalse(email);
            return account.orElse(null);
        }
        return null;
    }

    public String getIdCurrentAccount() {
        String email = getCurrentEmail();
        if (email != null) {
            Optional<Account> account = accountRepository.findByEmailAndIsDeletedFalse(email);
            return account.get().getId();
        }
        return null;
    }
}
