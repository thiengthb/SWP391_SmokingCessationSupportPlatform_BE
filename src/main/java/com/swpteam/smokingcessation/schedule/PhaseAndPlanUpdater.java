package com.swpteam.smokingcessation.schedule;

import com.swpteam.smokingcessation.domain.entity.*;
import com.swpteam.smokingcessation.domain.enums.PhaseStatus;
import com.swpteam.smokingcessation.domain.enums.PlanStatus;
import com.swpteam.smokingcessation.domain.enums.ScoreRule;
import com.swpteam.smokingcessation.service.interfaces.profile.IScoreService;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
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
    IScoreService scoreService;

    @Transactional
    @Scheduled(cron = "* */30 * * * *")
    public void updatePhasesAndPlans() {
        List<Setting> settings = settingService.getAllSetting();
        LocalDateTime now = LocalDateTime.now();

        for (Setting setting : settings) {
            Account account = setting.getAccount();
            LocalTime deadline = setting.getReportDeadline();
            Plan plan;
            //take accountId with its deadline
            try {
                plan = planService.findByAccountIdAndPlanStatusAndIsDeletedFalse(account.getId(), PlanStatus.ACTIVE);
                log.info("found active plan with accId:{}",plan.getAccount().getId());
            } catch (Exception e) {
                log.warn("No ACTIVE plan found for account: {}", account.getId());
                continue;
                //if plan not found move to next setting to find accountId and plan
            }

            //calculate phase if reach phase End Date and isAfter user deadline
            for (Phase phase : plan.getPhases()) {
                log.info("check lazy 1 passed");
                LocalDateTime phaseDeadline = LocalDateTime.of(phase.getEndDate(), deadline);
                if (now.isAfter(phaseDeadline) && phase.getSuccessRate() == null) {
                    List<RecordHabit> recordHabits = recordHabitService.findAllByAccountIdAndDateBetweenAndIsDeletedFalse(
                            account.getId(), phase.getStartDate(), phase.getEndDate()
                    );
                    log.info("found phases");
                    phaseService.calculateSuccessRateAndUpdatePhase(phase, recordHabits);
                }
            }

            if (allPhasesHaveCompleted(plan)) {
                log.info("check layz 2 passed");
                int totalPhases = plan.getPhases().size();
                int successCount = 0;
                double totalSuccessRate = 0;

                for (Phase phase : plan.getPhases()) {
                    if (phase.getPhaseStatus().equals(PhaseStatus.SUCCESS)) {
                        successCount++;
                    }
                }

                double successRatio = (double) successCount / totalPhases;

                PlanStatus planStatus = (successCount > totalPhases / 2) ? PlanStatus.COMPLETE : PlanStatus.FAILED;
                plan.setPlanStatus(planStatus);

                if (planStatus == PlanStatus.COMPLETE) {
                    log.info("score bonus for planSuccess:");
                    scoreService.updateScore(account.getId(), ScoreRule.PLAN_SUCCESS);
                }

                planService.updateCompletedPlan(plan, successRatio * 100.0, plan.getPlanStatus());
            }
            if (allPhasesFullyReported(plan, account.getId())) {
                log.info("check lazy 3 passed");
                log.info("earned REPORT_ALL_PLAN score");
                scoreService.updateScore(account.getId(), ScoreRule.REPORT_ALL_PLAN);
            }
        }
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void checkPendingPlans() {
        planService.dailyCheckingPlanStatus();
    }

    private boolean allPhasesHaveCompleted(Plan plan) {
        for (Phase phase : plan.getPhases()) {
            if (phase.getSuccessRate() == null) {
                return false;
            }
        }
        return true;

    }

    private boolean allPhasesFullyReported(Plan plan, String accountId) {
        for (Phase phase : plan.getPhases()) {
            List<RecordHabit> records = recordHabitService.findAllByAccountIdAndDateBetweenAndIsDeletedFalse(
                    accountId, phase.getStartDate(), phase.getEndDate()
            );
            long totalDays = ChronoUnit.DAYS.between(phase.getStartDate(),phase.getEndDate())+1;
            if (!phaseService.isPhaseFullyReported(totalDays, records)) {
                return false;
            }
        }
        return true;
    }
}