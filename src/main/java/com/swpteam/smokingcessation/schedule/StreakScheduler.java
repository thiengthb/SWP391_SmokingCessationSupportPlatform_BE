package com.swpteam.smokingcessation.schedule;

import com.swpteam.smokingcessation.constant.ErrorCode;
import com.swpteam.smokingcessation.domain.entity.Setting;
import com.swpteam.smokingcessation.domain.entity.Streak;
import com.swpteam.smokingcessation.domain.enums.ScoreRule;
import com.swpteam.smokingcessation.exception.AppException;
import com.swpteam.smokingcessation.repository.jpa.RecordHabitRepository;
import com.swpteam.smokingcessation.repository.jpa.SettingRepository;
import com.swpteam.smokingcessation.repository.jpa.StreakRepository;
import com.swpteam.smokingcessation.feature.version1.profile.service.IScoreService;
import com.swpteam.smokingcessation.feature.version1.tracking.service.IStreakService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StreakScheduler {
    SettingRepository settingRepository;
    RecordHabitRepository recordHabitRepository;
    StreakRepository streakRepository;
    IStreakService streakService;
    IScoreService scoreService;

    @Scheduled(cron = "0 */30 * * * *")
    public void checkAndResetStreak() {
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        List<Setting> settings = settingRepository.findAllByIsDeletedFalse();

        for (Setting setting : settings) {
            try {
                LocalTime deadline = setting.getReportDeadline();
                String accountId = setting.getAccount().getId();

                LocalDateTime deadlineDateTime = LocalDateTime.of(today, deadline);

                if (!now.isAfter(deadlineDateTime)) {
                    continue;
                }

                boolean hasRecord = recordHabitRepository
                        .existsByAccountIdAndDateAndIsDeletedFalse(accountId, today);

                Streak streak = streakRepository.findByAccountIdAndIsDeletedFalse(accountId)
                        .orElse(null);
                if (streak == null || hasRecord || streak.getNumber() == 0) {
                    continue;
                }

                streakService.resetStreak(accountId);
                scoreService.updateScore(accountId, ScoreRule.REPORT_DAY_MISS);
            } catch (Exception e) {
                log.error("Failed to reset streak for setting accountId: {}", setting.getAccount().getId(), e);
                throw new AppException(ErrorCode.STREAK_RESET_FAILED);
            }
        }
    }
}
