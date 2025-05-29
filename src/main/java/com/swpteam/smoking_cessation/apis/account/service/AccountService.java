package com.swpteam.smoking_cessation.apis.account.service;

import com.swpteam.smoking_cessation.apis.account.dto.request.AccountRequest;
import com.swpteam.smoking_cessation.apis.account.dto.response.AccountResponse;

import java.util.List;

public interface AccountService {
    AccountResponse createAccount(AccountRequest request);
    AccountResponse getAccountById(String id);
    List<AccountResponse> getAllAccounts();
    AccountResponse updateAccount(String id, AccountRequest request);
    void deleteAccount(String id);
}
