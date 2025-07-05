package com.swpteam.smokingcessation.schedule;

import com.swpteam.smokingcessation.repository.jpa.TokenRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TokenCleanUp {

    TokenRepository tokenRepository;

    @Transactional
    @Scheduled(fixedRate = 3600000)
    public void cleanExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        int deletedCount = tokenRepository.deleteAllByExpiryTimeBefore(now);
        log.info("Deleted {} expired tokens", deletedCount);
    }
}

