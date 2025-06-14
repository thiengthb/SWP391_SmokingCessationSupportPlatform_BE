package com.swpteam.smokingcessation.service.interfaces.identity;

import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.domain.dto.account.AccountRequest;
import com.swpteam.smokingcessation.domain.dto.account.AccountResponse;
import com.swpteam.smokingcessation.domain.dto.account.AccountUpdateRequest;
import com.swpteam.smokingcessation.domain.dto.account.ChangePasswordRequest;
import com.swpteam.smokingcessation.domain.enums.Role;
import org.springframework.data.domain.Page;

public interface IAccountService {

    AccountResponse createAccount(AccountRequest request);

    Page<AccountResponse> getAccounts(PageableRequest request);

    AccountResponse getAccountById(String id);

    AccountResponse updateAccountRole(String id, Role role);

    AccountResponse updateAccountWithoutRole(String id, AccountUpdateRequest request);

    void deleteAccount(String id);

    AccountResponse changePassword(ChangePasswordRequest request);

    AccountResponse getCurrentAccount();

    void banAccount(String id);
}
