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
import org.springframework.data.domain.Page;

public interface IAccountService {

    void updateStatus(String accountId, AccountStatus status);

    void changePassword(String accountId, String newPassword);

    PageResponse<AccountResponse> getAccountsPage(PageableRequest request);

    AccountResponse createAccount(AccountRequest request);

    Account createAccountByGoogle(GoogleIdToken.Payload payload);

    AccountResponse getAccountById(String id);

    AccountResponse updateAccountRole(String id, Role role);

    AccountResponse updateAccountWithoutRole(String id, AccountUpdateRequest request);

    AccountResponse changePassword(ChangePasswordRequest request);

    AccountResponse getCurrentAccount();

    Account findAccountByIdOrThrowError(String id);

    Account findAccountByUsernameOrThrowError(String username);

    Account findAccountByEmailOrThrowError(String email);

    void deleteAccount(String id);

    void banAccount(String id);

    void checkExistByEmail(String email);

}
