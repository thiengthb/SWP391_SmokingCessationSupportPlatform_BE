package com.swpteam.smokingcessation.service.impl.identity;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.swpteam.smokingcessation.common.PageResponse;
import com.swpteam.smokingcessation.domain.dto.account.AccountRequest;
import com.swpteam.smokingcessation.domain.dto.account.AccountResponse;
import com.swpteam.smokingcessation.domain.dto.account.AccountUpdateRequest;
import com.swpteam.smokingcessation.domain.dto.account.ChangePasswordRequest;
import com.swpteam.smokingcessation.domain.enums.AccountStatus;
import com.swpteam.smokingcessation.domain.enums.AuthProvider;
import com.swpteam.smokingcessation.domain.enums.Role;
import com.swpteam.smokingcessation.domain.mapper.AccountMapper;
import com.swpteam.smokingcessation.repository.AccountRepository;
import com.swpteam.smokingcessation.domain.entity.Setting;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.constant.ErrorCode;
import com.swpteam.smokingcessation.domain.entity.Account;
import com.swpteam.smokingcessation.exception.AppException;
import com.swpteam.smokingcessation.service.interfaces.identity.IAccountService;
import com.swpteam.smokingcessation.utils.AuthUtilService;
import com.swpteam.smokingcessation.utils.RandomUtil;
import com.swpteam.smokingcessation.utils.ValidationUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AccountServiceImpl implements IAccountService {

    AccountRepository accountRepository;
    AccountMapper accountMapper;

    AuthUtilService authUtilService;
    PasswordEncoder passwordEncoder;

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public PageResponse<AccountResponse> getAccountsPage(PageableRequest request) {
        ValidationUtil.checkFieldExist(Account.class, request.sortBy());

        Pageable pageable = PageableRequest.getPageable(request);
        Page<Account> accounts = accountRepository.findAllByIsDeletedFalse(pageable);

        return new PageResponse<>(accounts.map(accountMapper::toResponse));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public AccountResponse getAccountById(String id) {
        return accountMapper.toResponse(findAccountByIdOrThrowError(id));
    }

    @Override
    public AccountResponse getCurrentAccount() {
        return accountMapper.toResponse(authUtilService.getCurrentAccountOrThrowError());
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public AccountResponse createAccount(AccountRequest request) {
        checkExistByEmailOrThrowError(request.email());
        checkExistByPhoneNumber(request.phoneNumber());

        Account account = accountMapper.toEntity(request);
        account.setUsername(RandomUtil.generateRandomUsername());
        account.setPassword(passwordEncoder.encode(request.password()));
        account.setProvider(AuthProvider.LOCAL);
        account.setSetting(Setting.getDefaultSetting(account));

        return accountMapper.toResponse(accountRepository.save(account));
    }

    @Override
    @Transactional
    public Account createAccountByGoogle(GoogleIdToken.Payload payload) {
        String email = payload.getEmail();
        return accountRepository.findByEmail(email)
                .orElseGet(() -> {
                    Account newAccount = Account.builder()
                            .username(RandomUtil.generateRandomUsername())
                            .email(payload.getEmail())
                            .provider(AuthProvider.GOOGLE)
                            .role(Role.MEMBER)
                            .avatar((String) payload.get("picture"))
                            .build();
                    return accountRepository.save(newAccount);
                });
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public AccountResponse updateAccountRole(String id, Role role) {
        Account account = findAccountByIdOrThrowError(id);

        account.setRole(role);

        return accountMapper.toResponse(accountRepository.save(account));
    }

    @Override
    @Transactional
    public AccountResponse updateAccountWithoutRole(String id, AccountUpdateRequest request) {
        Account account = findAccountByIdOrThrowError(id);

        accountMapper.updateWithoutRole(account, request);
        if (request.password() != null) {
            account.setPassword(passwordEncoder.encode(request.password()));
        }

        return accountMapper.toResponse(accountRepository.save(account));
    }

    @Override
    @Transactional
    public void updateStatus(String accountId, AccountStatus status) {
        Account account = findAccountByIdOrThrowError(accountId);

        account.setStatus(AccountStatus.ONLINE);
        accountRepository.save(account);
    }

    @Override
    @Transactional
    public AccountResponse changePassword(ChangePasswordRequest request) {
        Account currentAccount = authUtilService.getCurrentAccountOrThrowError();

        if (!passwordEncoder.matches(request.oldPassword(), currentAccount.getPassword())) {
            throw new AppException(ErrorCode.WRONG_PASSWORD);
        }

        currentAccount.setPassword(passwordEncoder.encode(request.newPassword()));
        accountRepository.save(currentAccount);

        return accountMapper.toResponse(currentAccount);
    }

    @Override
    @Transactional
    public void changePassword(String accountId, String newPassword) {
        Account account = findAccountByIdOrThrowError(accountId);

        account.setPassword(passwordEncoder.encode(newPassword));

        accountRepository.save(account);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteAccount(String id) {
        Account account = findAccountByIdOrThrowError(id);

        account.setDeleted(true);

        accountRepository.save(account);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void banAccount(String id) {
        Account account = findAccountByIdOrThrowError(id);

        Account currentAccount = authUtilService.getCurrentAccountOrThrowError();
        if (account == currentAccount)
            throw new AppException(ErrorCode.SELF_BAN);

        account.setStatus(AccountStatus.BANNED);
        accountRepository.save(account);
    }

    @Override
    public Account findAccountByEmailOrThrowError(String email) {
        return accountRepository.findByEmailAndIsDeletedFalse(email)
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
    }

    @Override
    public Account findAccountByIdOrThrowError(String id) {
        return accountRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
    }

    @Override
    public void checkExistByEmailOrThrowError(String email) {
        if (accountRepository.existsByEmail(email))
            throw new AppException(ErrorCode.EMAIL_EXISTED);
    }

    private void checkExistByPhoneNumber(String phoneNumber) {
        if (accountRepository.existsByPhoneNumber(phoneNumber))
            throw new AppException(ErrorCode.PHONE_NUMBER_EXISTED);
    }

}
