package com.swpteam.smokingcessation.config;

import com.swpteam.smokingcessation.domain.entity.Account;
import com.swpteam.smokingcessation.domain.entity.Category;
import com.swpteam.smokingcessation.domain.entity.Member;
import com.swpteam.smokingcessation.repository.AccountRepository;
import com.swpteam.smokingcessation.domain.enums.Role;
import com.swpteam.smokingcessation.domain.entity.Setting;
import com.swpteam.smokingcessation.repository.CategoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApplicationInitConfig {

    @NonFinal
    @Value("${admin.email}")
    String adminEmail;

    @NonFinal
    @Value("${admin.password}")
    String defaultPassword;

    PasswordEncoder passwordEncoder;

    AccountRepository accountRepository;
    CategoryRepository categoryRepository;

    private static final String TEST_MEMBER_EMAIL = "member@gmail.com";
    private static final String TEST_MEMBER_PASS = "1";

    private static final String TEST_COACH_EMAIL = "coach@gmail.com";
    private static final String TEST_COACH_PASS = "1";

    public static final String DEFAULT_CATEGORY = "Uncategorized";

    @Bean
    @ConditionalOnProperty(
            prefix = "spring",
            value = "datasource.driver-class-name",
            havingValue = "com.mysql.cj.jdbc.Driver"
    )
    ApplicationRunner applicationRunner() {
        log.info("Initializing application ...");

        return args -> {

            if (accountRepository.findByEmail(adminEmail).isEmpty()) {
                makeDefaultAccount(adminEmail, defaultPassword, Role.ADMIN);
            }

            if (accountRepository.findByEmail(TEST_MEMBER_EMAIL).isEmpty()) {
                makeDefaultAccount(TEST_MEMBER_EMAIL, TEST_MEMBER_PASS, Role.MEMBER);
            }

            if (categoryRepository.findByName(DEFAULT_CATEGORY).isEmpty()) {
                makeDefaultUncategorized();
            }

            log.info("Application initialization completed ...");
        };
    }

    private void makeDefaultAccount(String email, String password, Role role) {
        Account account = Account.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(role)
                .build();

        Setting setting = Setting.getDefaultSetting(account);

        account.setSetting(setting);
        accountRepository.save(account);

        log.info("An {} account has been created with email: {}, default password: {}. Please change the password immediately.", role.name().toLowerCase(), email, password);
    }

    private void makeDefaultMemberAccount() {
        Account account = Account.builder()
                .email(ApplicationInitConfig.TEST_MEMBER_EMAIL)
                .password(passwordEncoder.encode(ApplicationInitConfig.TEST_MEMBER_PASS))
                .role(Role.MEMBER)
                .build();

        Setting setting = Setting.getDefaultSetting(account);
        Member member = Member.getDefaultMember(account);

        account.setSetting(setting);
        account.setMember(member);
        accountRepository.save(account);

        log.info("An member account has been created with email: {}, default password: {}. Please change the password immediately.", ApplicationInitConfig.TEST_MEMBER_EMAIL, ApplicationInitConfig.TEST_MEMBER_PASS);
    }

    private void makeDefaultUncategorized() {
        Category uncategorized = Category.builder()
                .name(DEFAULT_CATEGORY)
                .build();

        categoryRepository.save(uncategorized);
    }
}
