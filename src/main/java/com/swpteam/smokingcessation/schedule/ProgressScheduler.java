package com.swpteam.smokingcessation.schedule;

import com.swpteam.smokingcessation.domain.entity.*;
import com.swpteam.smokingcessation.domain.enums.TrackingMode;
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

    @Transactional
    @Scheduled(cron = "0 * * * * *")
    public void calculateProgress(){
        List<Setting> settings = settingRepository.findAllByIsDeletedFalse();
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        for(Setting setting : settings){
            Health health = healthService.findLatestHealthByAccountIdOrNull(setting.getAccount().getId());
            if(health == null){
                continue;
            }
            Member member = setting.getAccount().getMember();

            LocalTime deadline = setting.getReportDeadline();
            LocalDateTime deadlineDateTime = LocalDateTime.of(today, deadline);

            if(setting.getTrackingMode().equals(TrackingMode.DAILY_RECORD)){
                if(!recordHabitService.checkHabitRecordExistence(member.getId(), today)){
                    continue;
                }
                if(now.isAfter(deadlineDateTime)){
                    RecordHabit recordHabit = recordHabitService.findRecordByDateOrNull(member.getId(), today);
                    if(recordHabit == null){
                        continue;
                    }
                    int avoidedCigarettes = health.getCigarettesPerDay() - recordHabit.getCigarettesSmoked();
                    double moneySaved = avoidedCigarettes * (health.getPackPrice() / health.getCigarettesPerPack());

                    member.setCigarettesAvoided(member.getCigarettesAvoided() + avoidedCigarettes);
                    member.setMoneySaved(member.getMoneySaved() + moneySaved);
                    continue;
                }
            }
            double avoidedCigarettes = (double) health.getCigarettesPerDay() / 24;
            double moneySaved = avoidedCigarettes * (health.getPackPrice() / health.getCigarettesPerPack());

            member.setCigarettesAvoided(member.getCigarettesAvoided() + avoidedCigarettes);
            member.setMoneySaved(member.getMoneySaved() + moneySaved);
        }
    }
}
