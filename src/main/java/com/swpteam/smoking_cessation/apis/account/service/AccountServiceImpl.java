package com.swpteam.smoking_cessation.apis.account.service;

import com.swpteam.smoking_cessation.apis.account.dto.request.AccountRequest;
import com.swpteam.smoking_cessation.apis.account.dto.response.AccountResponse;
import com.swpteam.smoking_cessation.apis.account.entity.Account;
import com.swpteam.smoking_cessation.apis.account.mapper.AccountMapper;
import com.swpteam.smoking_cessation.apis.account.repository.AccountRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@RequiredArgsConstructor
@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    private final AccountMapper accountMapper;

    @Override
    public AccountResponse createAccount(AccountRequest request) {
        Account account = accountMapper.toEntity(request);
        account = accountRepository.save(account);
        return accountMapper.toResponse(account);
    }

    @Override
    public AccountResponse getAccountById(String id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tài khoản không tồn tại với id: " + id));
        return accountMapper.toResponse(account);
    }

    @Override
    public List<AccountResponse> getAllAccounts() {
        List<Account> accounts = accountRepository.findAll();
        return accountMapper.toResponseList(accounts);
    }

    @Override
    public AccountResponse updateAccount(String id, AccountRequest request) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tài khoản không tồn tại với id: " + id));
        accountMapper.updateEntity(account, request);
        account = accountRepository.save(account);
        return accountMapper.toResponse(account);
    }

    @Override
    public void deleteAccount(String id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tài khoản không tồn tại với id: " + id));
        account.setIsDeleted(true);
        accountRepository.save(account);
    }
}