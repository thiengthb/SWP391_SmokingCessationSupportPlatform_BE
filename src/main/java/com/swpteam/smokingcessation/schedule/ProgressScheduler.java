package com.swpteam.smokingcessation.schedule;

import com.swpteam.smokingcessation.domain.entity.Health;
import com.swpteam.smokingcessation.domain.entity.Member;
import com.swpteam.smokingcessation.domain.entity.RecordHabit;
import com.swpteam.smokingcessation.domain.entity.Setting;
import com.swpteam.smokingcessation.repository.MemberRepository;
import com.swpteam.smokingcessation.repository.RecordHabitRepository;
import com.swpteam.smokingcessation.repository.SettingRepository;
import com.swpteam.smokingcessation.service.interfaces.profile.IHealthService;
import com.swpteam.smokingcessation.service.interfaces.tracking.IRecordHabitService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProgressScheduler {

    SettingRepository settingRepository;
    IHealthService healthService;
    IRecordHabitService recordHabitService;
    MemberRepository memberRepository;
    RecordHabitRepository recordHabitRepository;

    @Transactional
    @Scheduled(cron = "0 */30 * * * *")
    public void calculateProgress() {
        List<Setting> settings = settingRepository.findAllByIsDeletedFalse();

        for (Setting setting : settings) {

            Member member = setting.getAccount().getMember();

            Health health = healthService.findLatestHealthByAccountIdOrNull(setting.getAccount().getId());
            if (health == null) {
                continue;
            }

            switch (setting.getTrackingMode()) {
                case DAILY_RECORD -> progressDailyRecord(setting, member, health);
                case AUTO_COUNT -> progressAutoCount(member, health);
            }
        }
    }

    private void progressDailyRecord(Setting setting, Member member, Health health) {
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        LocalTime deadline = setting.getReportDeadline();
        LocalDateTime deadlineDateTime = LocalDateTime.of(today, deadline);

        if (now.isBefore(deadlineDateTime)) return;

        if (!recordHabitService.checkHabitRecordExistence(member.getId(), today)) {
            return;
        }
        RecordHabit recordHabit = recordHabitService.findRecordByDateOrNull(member.getId(), today);

        if (!recordHabit.isProgressed()) {
            double avoidedCigarettes = health.getCigarettesPerDay() - recordHabit.getCigarettesSmoked();
            double moneySaved = avoidedCigarettes * (health.getPackPrice() / health.getCigarettesPerPack());

            member.setCigarettesAvoided(member.getCigarettesAvoided() + avoidedCigarettes);
            member.setMoneySaved(member.getMoneySaved() + moneySaved);

            recordHabit.setProgressed(true);
            recordHabitRepository.save(recordHabit);
            memberRepository.save(member);
        }
    }

    private void progressAutoCount(Member member, Health health) {
        double avoidedCigarettes = (double) health.getCigarettesPerDay() / 24;
        double moneySaved = avoidedCigarettes * (health.getPackPrice() / health.getCigarettesPerPack());

        member.setCigarettesAvoided(member.getCigarettesAvoided() + avoidedCigarettes);
        member.setMoneySaved(member.getMoneySaved() + moneySaved);

        memberRepository.save(member);
    }
}
