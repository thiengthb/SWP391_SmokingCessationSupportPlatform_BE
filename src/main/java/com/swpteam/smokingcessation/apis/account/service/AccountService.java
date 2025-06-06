package com.swpteam.smokingcessation.apis.account.service;

import com.swpteam.smokingcessation.apis.account.dto.request.AccountCreateRequest;
import com.swpteam.smokingcessation.apis.account.dto.request.AccountUpdateRequest;
import com.swpteam.smokingcessation.apis.account.dto.request.ChangePasswordRequest;
import com.swpteam.smokingcessation.apis.account.dto.response.AccountResponse;
import com.swpteam.smokingcessation.apis.account.entity.Account;
import com.swpteam.smokingcessation.apis.account.mapper.AccountMapper;
import com.swpteam.smokingcessation.apis.account.repository.AccountRepository;
import com.swpteam.smokingcessation.exception.AppException;
import com.swpteam.smokingcessation.exception.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AccountService {

    private AccountRepository accountRepository;
    private AccountMapper accountMapper;

    public AccountResponse createAccount(AccountCreateRequest request) {
        if (accountRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.ACCOUNT_EXISTED);
        }

        Account account = accountMapper.toAccount(request);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        account.setPassword(passwordEncoder.encode(request.getPassword()));

        return accountMapper.toAccountResponse(accountRepository.save(account));
    }

    public List<Account> getAccounts() {
        return accountRepository.findAllByIsDeletedFalse();
    }

    public AccountResponse getAccountById(String id) {
        return accountMapper.toAccountResponse(findAccountById(id));
    }

    private Account findAccountById(String id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_EXISTED));
        if (account.isDeleted()) {
            throw new AppException(ErrorCode.ACCOUNT_DELETED);
        }
        return account;
    }


    @Transactional
    public AccountResponse updateAccount(AccountUpdateRequest request, String id) {
        Account account = findAccountById(id);

        accountMapper.updateAccount(account, request);
        accountRepository.save(account);

        return accountMapper.toAccountResponse(account);
    }

    public void deleteAccount(String id) {
        Account account = findAccountById(id);

        account.setDeleted(true);
        accountRepository.save(account);
    }

    @Transactional
    public AccountResponse changePassword(ChangePasswordRequest request) {
        Account account = accountRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_EXISTED));

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

        if (!passwordEncoder.matches(request.getOldPassword(), account.getPassword())) {
            throw new AppException(ErrorCode.WRONG_PASSWORD);
        }

        account.setPassword(passwordEncoder.encode(request.getNewPassword()));
        accountRepository.save(account);

        return accountMapper.toAccountResponse(account);
    }

    public AccountResponse getAccountByEmail(String email) {
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_EXISTED));
        return accountMapper.toAccountResponse(account);
    }
}
