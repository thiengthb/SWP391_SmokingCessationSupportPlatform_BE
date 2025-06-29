package com.swpteam.smokingcessation.initialize;

import com.swpteam.smokingcessation.domain.entity.Account;
import com.swpteam.smokingcessation.domain.entity.Setting;
import com.swpteam.smokingcessation.domain.enums.AccountStatus;
import com.swpteam.smokingcessation.domain.enums.AuthProvider;
import com.swpteam.smokingcessation.domain.enums.Role;
import com.swpteam.smokingcessation.repository.AccountRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AccountInit implements CommandLineRunner {

    @NonFinal
    @Value("${admin.email}")
    String adminEmail;

    @NonFinal
    @Value("${admin.password}")
    String defaultPassword;

    AccountRepository accountRepository;
    PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (accountRepository.count() == 0 || accountRepository.findByEmail(adminEmail).isEmpty()) {
            Account account = Account.builder()
                    .username(Role.ADMIN.name())
                    .password(passwordEncoder.encode(defaultPassword))
                    .email(adminEmail)
                    .provider(AuthProvider.LOCAL)
                    .role(Role.ADMIN)
                    .status(AccountStatus.OFFLINE)
                    .build();

            account.setSetting(Setting.getDefaultSetting(account));

            accountRepository.save(account);

            log.info("An admin account ({}) have been created with default password, please login and change the password", adminEmail);
        }
    }

}
