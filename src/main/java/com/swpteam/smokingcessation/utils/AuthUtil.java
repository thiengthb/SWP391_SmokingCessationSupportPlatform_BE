package com.swpteam.smokingcessation.utils;

import com.swpteam.smokingcessation.constant.ErrorCode;
import com.swpteam.smokingcessation.domain.entity.Account;
import com.swpteam.smokingcessation.domain.enums.Role;
import com.swpteam.smokingcessation.exception.AppException;
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
public class AuthUtil {

    AccountRepository accountRepository;

    public boolean checkAdminOrOwner(String ownerId) {
        Account currentAccount = getCurrentAccount()
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));

        boolean isAdmin = currentAccount.getRole() == Role.ADMIN;
        boolean isOwner = currentAccount.getId().equals(ownerId);

        return isAdmin || isOwner;
    }

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
