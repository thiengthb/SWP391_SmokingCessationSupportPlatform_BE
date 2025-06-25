package com.swpteam.smokingcessation.schedule;

import com.swpteam.smokingcessation.domain.entity.*;
import com.swpteam.smokingcessation.domain.enums.PlanStatus;
import com.swpteam.smokingcessation.service.interfaces.profile.ISettingService;
import com.swpteam.smokingcessation.service.interfaces.tracking.IPhaseService;
import com.swpteam.smokingcessation.service.interfaces.tracking.IPlanService;
import com.swpteam.smokingcessation.service.interfaces.tracking.IRecordHabitService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PhaseAndPlanUpdater {

    ISettingService settingService;
    IPlanService planService;
    IPhaseService phaseService;
    IRecordHabitService recordHabitService;

    @Scheduled(cron = "0 * * * * *") // Chạy mỗi phút
    public void updatePhasesAndPlans() {
        List<Setting> settings = settingService.getAllSetting();
        LocalDateTime now = LocalDateTime.now();

        for (Setting setting : settings) {
            Account account = setting.getAccount();
            LocalTime deadline = setting.getReportDeadline();

            Plan plan;
            try {
                plan = planService.findByAccountIdAndPlanStatusAndIsDeletedFalse(account.getId(), PlanStatus.ACTIVE);
            } catch (Exception e) {
                log.warn("No ACTIVE plan found for account: {}", account.getId());
                continue;
            }

            for (Phase phase : plan.getPhases()) {
                LocalDateTime phaseDeadline = LocalDateTime.of(phase.getEndDate(), deadline);
                if (now.isAfter(phaseDeadline) && phase.getSuccessRate() == null) {
                    List<RecordHabit> recordHabits = recordHabitService.findAllByAccountIdAndDateBetweenAndIsDeletedFalse(
                            account.getId(), phase.getStartDate(), phase.getEndDate()
                    );
                    phaseService.calculateSuccessRateAndUpdatePhase(phase, recordHabits);
                }
            }
        }
    }
}