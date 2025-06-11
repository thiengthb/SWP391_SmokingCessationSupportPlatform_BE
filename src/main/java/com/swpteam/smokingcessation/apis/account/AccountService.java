package com.swpteam.smokingcessation.apis.account;

import com.swpteam.smokingcessation.apis.account.dto.AccountCreateRequest;
import com.swpteam.smokingcessation.apis.account.dto.AccountResponse;
import com.swpteam.smokingcessation.apis.account.dto.AccountUpdateRequest;
import com.swpteam.smokingcessation.apis.account.dto.ChangePasswordRequest;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AccountService {

    AccountRepository accountRepository;
    SettingRepository settingRepository;
    MemberRepository memberRepository;
    AccountMapper accountMapper;


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

        setting.setAccount(account);
        settingRepository.save(Setting.getDefaultSetting(account));

        healthRepository.save(Health.getDefaultHealth(account));

        member.setAccount(account);
        memberRepository.save(Member().getDefaultMember());

        return accountMapper.toResponse(accountRepository.save(account));
    }
    public boolean isAccountOwnedByUser(String accountId, String email) {
        return accountRepository.findById(accountId)
                .map(account -> account.getEmail().equals(email) && !account.isDeleted())
                .orElse(false);
    }

    public Page<AccountResponse> getAccounts(PageableRequest request) {
        if (!ValidationUtil.isFieldExist(Account.class, request.getSortBy())) {
            throw new AppException(ErrorCode.INVALID_SORT_FIELD);
        }

        Pageable pageable = PageableRequest.getPageable(request);
        Page<Account> accounts = accountRepository.findAllByIsDeletedFalse(pageable);

        return accounts.map(accountMapper::toResponse);
    }

    public AccountResponse getAccountById(String id) {
        return accountMapper.toResponse(findAccountById(id));
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
    public AccountResponse updateAccount(String id, AccountRequest request) {
        Account account = findAccountById(id);

        accountMapper.update(account, request);
        accountRepository.save(account);

        return accountMapper.toResponse(account);
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

        return accountMapper.toResponse(account);
    }

    public AccountResponse getAccountByEmail(String email) {
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_EXISTED));
        return accountMapper.toResponse(account);
    }
}
