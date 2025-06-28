package com.swpteam.smokingcessation.service.interfaces.identity;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.swpteam.smokingcessation.common.PageResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.domain.dto.account.AccountRequest;
import com.swpteam.smokingcessation.domain.dto.account.AccountResponse;
import com.swpteam.smokingcessation.domain.dto.account.AccountUpdateRequest;
import com.swpteam.smokingcessation.domain.dto.account.ChangePasswordRequest;
import com.swpteam.smokingcessation.domain.entity.Account;
import com.swpteam.smokingcessation.domain.enums.AccountStatus;
import com.swpteam.smokingcessation.domain.enums.Role;

public interface IAccountService {

    PageResponse<AccountResponse> getAccountsPage(PageableRequest request);

    AccountResponse getAccountById(String id);

    AccountResponse getCurrentAccount();

    void verifyAccount(String accountId);

    AccountResponse createAccount(AccountRequest request);

    Account createAccountByGoogle(GoogleIdToken.Payload payload);

    void updateStatus(String accountId, AccountStatus status);

    AccountResponse updateAccountRole(String id, Role role);

    AccountResponse updateAccountWithoutRole(String id, AccountUpdateRequest request);

    AccountResponse changePassword(ChangePasswordRequest request);

    void changePassword(String accountId, String newPassword);

    Account findAccountByIdOrThrowError(String id);

    Account findAccountByEmailOrThrowError(String email);

    void deleteAccount(String id);

    void banAccount(String id);

    void checkExistByEmailOrThrowError(String email);

}
