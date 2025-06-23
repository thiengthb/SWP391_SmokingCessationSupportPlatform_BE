package com.swpteam.smokingcessation.schedule;

import com.swpteam.smokingcessation.constant.ErrorCode;
import com.swpteam.smokingcessation.domain.entity.Member;
import com.swpteam.smokingcessation.domain.entity.Setting;
import com.swpteam.smokingcessation.domain.entity.Streak;
import com.swpteam.smokingcessation.exception.AppException;
import com.swpteam.smokingcessation.repository.MemberRepository;
import com.swpteam.smokingcessation.repository.RecordHabitRepository;
import com.swpteam.smokingcessation.repository.SettingRepository;
import com.swpteam.smokingcessation.repository.StreakRepository;
import com.swpteam.smokingcessation.service.interfaces.profile.IMemberService;
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
    MemberRepository memberRepository;
    IMemberService memberService;

    @Scheduled(cron = "0 * * * * *")
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
                    return;
                }

                boolean hasRecord = recordHabitRepository.existsByAccountIdAndDateAndIsDeletedFalse(accountId, today);

                Streak streak = streakRepository.findByAccountIdAndIsDeletedFalse(accountId)
                        .orElse(null);
                if (streak == null || hasRecord || streak.getNumber() == 0) {
                    return;
                }

                Member member = memberService.findMemberByIdOrThrowError(streak.getAccount().getId());

                if (member.getHighestStreak() < streak.getNumber()) {
                    member.setHighestStreak(streak.getNumber());
                    memberRepository.save(member);
                }
                streak.setNumber(0);
                streakRepository.save(streak);
            } catch (Exception e) {
                log.error("Failed to reset streak for setting accountId: {}", setting.getAccount().getId(), e);
                throw new AppException(ErrorCode.STREAK_RESET_FAILED);
            }
        }
    }
}
