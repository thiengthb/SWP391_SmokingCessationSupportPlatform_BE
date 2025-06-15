package com.swpteam.smokingcessation.utils;

import com.swpteam.smokingcessation.constant.ErrorCode;
import com.swpteam.smokingcessation.domain.entity.Account;
import com.swpteam.smokingcessation.domain.enums.Role;
import com.swpteam.smokingcessation.exception.AppException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthorizationUtilService {

    AccountUtilService accountUtilService;

    public boolean checkAdminOrOwner(String ownerId) {
        Account currentAccount = accountUtilService.getCurrentAccount()
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));

        boolean isAdmin = currentAccount.getRole() == Role.ADMIN;
        boolean isOwner = currentAccount.getId().equals(ownerId);

        return isAdmin || isOwner;
    }
}
