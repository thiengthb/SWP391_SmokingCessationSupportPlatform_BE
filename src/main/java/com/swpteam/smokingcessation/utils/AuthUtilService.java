package com.swpteam.smokingcessation.utils;

import com.swpteam.smokingcessation.constant.ErrorCode;
import com.swpteam.smokingcessation.domain.entity.Account;
import com.swpteam.smokingcessation.domain.enums.Role;
import com.swpteam.smokingcessation.exception.AppException;
import com.swpteam.smokingcessation.repository.AccountRepository;
import com.swpteam.smokingcessation.security.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

@Slf4j
@Service("authUtilService")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthUtilService {

    AccountRepository accountRepository;

    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    public Optional<UserPrincipal> getCurrentPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal principal) {
            return Optional.of(principal);
        }
        return Optional.empty();
    }

    public Optional<String> getCurrentAccountId() {
        return getCurrentPrincipal().map(UserPrincipal::getId);
    }

    public Optional<Account> getCurrentAccount() {
        return getCurrentAccountId()
                .flatMap(accountRepository::findById);
    }

    public Account getCurrentAccountOrThrowError() {
        return getCurrentAccount()
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
    }

    public boolean isAdminOrOwner(String ownerId) {
        Account account = getCurrentAccountOrThrowError();
        return account.getRole() == Role.ADMIN || account.getId().equals(ownerId);
    }

    public boolean isOwner(String ownerId) {
        Account account = getCurrentAccountOrThrowError();
        return account.getId().equals(ownerId);
    }

    public boolean isAdmin() {
        return getCurrentAccount()
                .map(account -> account.getRole() == Role.ADMIN)
                .orElse(false);
    }

    public Optional<String> getCurrentAccessToken() {
        HttpServletRequest request = getCurrentHttpRequest();
        if (request == null) return Optional.empty();

        String header = request.getHeader(AUTH_HEADER);
        if (header != null && header.startsWith(BEARER_PREFIX)) {
            return Optional.of(header.substring(BEARER_PREFIX.length()));
        }
        return Optional.empty();
    }

    private HttpServletRequest getCurrentHttpRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes servletRequestAttributes) {
            return servletRequestAttributes.getRequest();
        }
        return null;
    }

}
