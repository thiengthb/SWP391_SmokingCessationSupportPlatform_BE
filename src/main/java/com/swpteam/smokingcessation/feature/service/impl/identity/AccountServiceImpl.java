package com.swpteam.smokingcessation.feature.service.impl.identity;

import com.swpteam.smokingcessation.domain.dto.account.AccountRequest;
import com.swpteam.smokingcessation.domain.dto.account.AccountResponse;
import com.swpteam.smokingcessation.domain.dto.account.AccountUpdateRequest;
import com.swpteam.smokingcessation.domain.dto.account.ChangePasswordRequest;
import com.swpteam.smokingcessation.domain.enums.AccountStatus;
import com.swpteam.smokingcessation.domain.enums.Role;
import com.swpteam.smokingcessation.domain.entity.Health;
import com.swpteam.smokingcessation.domain.mapper.AccountMapper;
import com.swpteam.smokingcessation.feature.repository.AccountRepository;
import com.swpteam.smokingcessation.feature.repository.HealthRepository;
import com.swpteam.smokingcessation.domain.entity.Member;
import com.swpteam.smokingcessation.feature.repository.MemberRepository;
import com.swpteam.smokingcessation.domain.entity.Setting;
import com.swpteam.smokingcessation.feature.repository.SettingRepository;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.constant.ErrorCode;
import com.swpteam.smokingcessation.domain.entity.Account;
import com.swpteam.smokingcessation.exception.AppException;
import com.swpteam.smokingcessation.feature.service.interfaces.identity.AccountService;
import com.swpteam.smokingcessation.utils.AccountUtil;
import com.swpteam.smokingcessation.utils.ValidationUtil;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AccountServiceImpl implements AccountService {

    AccountRepository accountRepository;
    SettingRepository settingRepository;
    HealthRepository healthRepository;
    MemberRepository memberRepository;
    AccountMapper accountMapper;
    AccountUtil accountUtil;


    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public AccountResponse createAccount(AccountRequest request) {
        if (accountRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.ACCOUNT_EXISTED);
        }

        if (accountRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new AppException(ErrorCode.PHONE_NUMBER_EXISTED);
        }
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

        Account account = accountMapper.toEntity(request);
        account.setPassword(passwordEncoder.encode(request.getPassword()));

        switch (account.getRole()) {
            case Role.MEMBER -> {
                healthRepository.save(Health.getDefaultHealth(account));
                memberRepository.save(Member.getDefaultMember(account));
            }
            case Role.COACH -> {

            }
            default -> {

            }
        }

        settingRepository.save(Setting.getDefaultSetting(account));

        return accountMapper.toResponse(accountRepository.save(account));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public Page<AccountResponse> getAccounts(PageableRequest request) {
        if (!ValidationUtil.isFieldExist(Account.class, request.getSortBy())) {
            throw new AppException(ErrorCode.INVALID_SORT_FIELD);
        }

        Pageable pageable = PageableRequest.getPageable(request);
        Page<Account> accounts = accountRepository.findAllByIsDeletedFalse(pageable);

        return accounts.map(accountMapper::toResponse);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public AccountResponse getAccountById(String id) {
        return accountMapper.toResponse(findAccountById(id));
    }

    private Account findAccountById(String id) {
        Account account = accountRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
        if (account.isDeleted()) {
            throw new AppException(ErrorCode.ACCOUNT_DELETED);
        }
        return account;
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public AccountResponse updateAccountRole(String id, Role role) {
        Account account = findAccountById(id);
        account.setRole(role);
        accountRepository.save(account);
        return accountMapper.toResponse(account);
    }

    @Override
    @Transactional
    public AccountResponse updateAccountWithoutRole(String id, AccountUpdateRequest request) {
        Account account = findAccountById(id);

        accountMapper.updateWithoutRole(account, request);

        if (request.getPassword() != null) {
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
            account.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        accountRepository.save(account);

        return accountMapper.toResponse(account);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteAccount(String id) {
        Account account = findAccountById(id);
        account.setDeleted(true);
        accountRepository.save(account);
    }

    @Override
    @Transactional
    public AccountResponse changePassword(ChangePasswordRequest request) {
        Account account = accountUtil.getCurrentAccount();

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

        if (!passwordEncoder.matches(request.getOldPassword(), account.getPassword())) {
            throw new AppException(ErrorCode.WRONG_PASSWORD);
        }

        account.setPassword(passwordEncoder.encode(request.getNewPassword()));
        accountRepository.save(account);

        return accountMapper.toResponse(account);
    }

    @Override
    public AccountResponse getAccountByEmail() {
        Account account = accountUtil.getCurrentAccount();
        return accountMapper.toResponse(account);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void banAccount(String id) {
        Account account = accountRepository.findByIdAndIsDeletedFalse(id).orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));

        String emailFromToken = accountUtil.getCurrentEmail();
        if (emailFromToken.equals(account.getEmail())) {
            throw new AppException(ErrorCode.SELF_BAN);
        }
        account.setStatus(AccountStatus.BANNED);
        accountRepository.save(account);
    }
}
