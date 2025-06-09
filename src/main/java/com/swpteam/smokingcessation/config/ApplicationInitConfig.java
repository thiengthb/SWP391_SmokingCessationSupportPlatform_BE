package com.swpteam.smokingcessation.config;

import com.swpteam.smokingcessation.apis.account.Account;
import com.swpteam.smokingcessation.apis.account.AccountRepository;
import com.swpteam.smokingcessation.apis.account.enums.Role;
import com.swpteam.smokingcessation.apis.member.Member;
import com.swpteam.smokingcessation.apis.member.MemberRepository;
import com.swpteam.smokingcessation.apis.setting.Setting;
import com.swpteam.smokingcessation.apis.setting.SettingRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {
    static final String ADMIN_EMAIL = "admin@gmail.com";
    static final String ADMIN_PASSWORD = "adminpassword";
    final PasswordEncoder passwordEncoder;
    SettingRepository settingRepository;
    MemberRepository memberRepository;

    @Bean
    @ConditionalOnProperty(
            prefix = "spring",
            value = "datasource.driver-class-name",
            havingValue = "com.mysql.cj.jdbc.Driver")
    ApplicationRunner applicationRunner(AccountRepository accountRepository) {
        log.info("Initializing application.....");
        return args -> {
            if (accountRepository.findByEmail(ADMIN_EMAIL).isEmpty()) {
                Account account = Account.builder()
                        .email(ADMIN_EMAIL)
                        .password(passwordEncoder.encode(ADMIN_PASSWORD))
                        .createdAt(LocalDateTime.now())
                        .role(Role.ADMIN)
                        .build();


                Setting setting = new Setting().getDefaultSetting();
                setting.setAccount(account);

                Member member = new Member().getDefaultMember();
                member.setAccount(account);

                account.setSetting(setting);
                account.setMember(member);

                accountRepository.save(account);

                log.warn("admin user has been created with default password: admin, please change it");
            }
            log.info("Application initialization completed .....");
        };
    }
}
