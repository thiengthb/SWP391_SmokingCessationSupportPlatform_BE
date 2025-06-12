package com.swpteam.smokingcessation.apis.account;

import com.swpteam.smokingcessation.apis.account.dto.AccountRequest;
import com.swpteam.smokingcessation.apis.account.dto.AccountResponse;
import com.swpteam.smokingcessation.apis.account.dto.AccountUpdateRequest;
import com.swpteam.smokingcessation.apis.account.dto.ChangePasswordRequest;
import com.swpteam.smokingcessation.apis.account.enums.AccountStatus;
import com.swpteam.smokingcessation.apis.account.enums.Role;
import com.swpteam.smokingcessation.apis.health.Health;
import com.swpteam.smokingcessation.apis.health.HealthRepository;
import com.swpteam.smokingcessation.apis.member.Member;
import com.swpteam.smokingcessation.apis.member.MemberRepository;
import com.swpteam.smokingcessation.apis.setting.Setting;
import com.swpteam.smokingcessation.apis.setting.SettingRepository;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.constants.ErrorCode;
import com.swpteam.smokingcessation.exception.AppException;
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
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AccountService {

    AccountRepository accountRepository;
    SettingRepository settingRepository;
    HealthRepository healthRepository;
    MemberRepository memberRepository;
    AccountMapper accountMapper;


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

    @PreAuthorize("hasRole('ADMIN')")
    public Page<AccountResponse> getAccounts(PageableRequest request) {
        if (!ValidationUtil.isFieldExist(Account.class, request.getSortBy())) {
            throw new AppException(ErrorCode.INVALID_SORT_FIELD);
        }

        Pageable pageable = PageableRequest.getPageable(request);
        Page<Account> accounts = accountRepository.findAllByIsDeletedFalse(pageable);

        return accounts.map(accountMapper::toResponse);
    }

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


    public AccountResponse updateAccount(String id, AccountUpdateRequest request, Jwt jwt) {
        return jwt.getClaimAsString("scope").equals("ROLE_ADMIN") ?
                updateAccountRole(id, request) : updateAccountWithoutRole(id, request);
    }

    @Transactional
    private AccountResponse updateAccountWithoutRole(String id, AccountUpdateRequest request) {
        Account account = accountRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));

        accountMapper.updateWithoutRole(account, request);
        if(request.getPassword() != null){
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
            account.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        accountRepository.save(account);

        return accountMapper.toResponse(account);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    private AccountResponse updateAccountRole(String id, AccountUpdateRequest request) {
        Account account = findAccountById(id);

        accountMapper.update(account, request);
        if(request.getPassword() != null){
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
            account.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        accountRepository.save(account);

        return accountMapper.toResponse(account);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteAccount(String id) {
        Account account = findAccountById(id);
        account.setDeleted(true);
        accountRepository.save(account);
    }

    @Transactional
    public AccountResponse changePassword(ChangePasswordRequest request) {
        Account account = accountRepository.findByEmailAndIsDeletedFalse(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

        if (!passwordEncoder.matches(request.getOldPassword(), account.getPassword())) {
            throw new AppException(ErrorCode.WRONG_PASSWORD);
        }

        account.setPassword(passwordEncoder.encode(request.getNewPassword()));
        accountRepository.save(account);

        return accountMapper.toResponse(account);
    }

    public AccountResponse getAccountByEmail(String email) {
        Account account = accountRepository.findByEmailAndIsDeletedFalse(email)
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
        return accountMapper.toResponse(account);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void banAccount(String id, Jwt jwt) {
        Account account = accountRepository.findByIdAndIsDeletedFalse(id).orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));

        if (jwt.getClaimAsString("sub").equals(account.getEmail())) {
            throw new AppException(ErrorCode.SELF_BAN);
        }
        account.setStatus(AccountStatus.BANNED);
        accountRepository.save(account);
    }
}
