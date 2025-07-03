package com.swpteam.smokingcessation.schedule;

import com.swpteam.smokingcessation.domain.entity.*;
import com.swpteam.smokingcessation.domain.enums.AccountStatus;
import com.swpteam.smokingcessation.domain.enums.PhaseStatus;
import com.swpteam.smokingcessation.domain.enums.PlanStatus;
import com.swpteam.smokingcessation.domain.enums.ScoreRule;
import com.swpteam.smokingcessation.feature.integration.mail.IMailService;
import com.swpteam.smokingcessation.feature.version1.notification.service.INotificationService;
import com.swpteam.smokingcessation.feature.version1.profile.service.IScoreService;
import com.swpteam.smokingcessation.feature.version1.profile.service.ISettingService;
import com.swpteam.smokingcessation.feature.version1.tracking.service.IPhaseService;
import com.swpteam.smokingcessation.feature.version1.tracking.service.IPlanService;
import com.swpteam.smokingcessation.feature.version1.tracking.service.IRecordHabitService;
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
    INotificationService notificationService;
    IMailService mailService;

    @Transactional
    @Scheduled(cron = "*/30 * * * * *")
    public void updatePhasesAndPlans() {
        List<Setting> settings = settingService.getAllSetting();
        LocalDateTime now = LocalDateTime.now();

        //get account and account's deadline
        for (Setting setting : settings) {
            Account account = setting.getAccount();
            LocalTime deadline = setting.getReportDeadline();
            Plan plan;

            //find plan using accountId and plan status is ACTIVE (1 account only have 1 active plan)
            try {
                plan = planService.findByAccountIdAndPlanStatusAndIsDeletedFalse(account.getId(), PlanStatus.ACTIVE);
            } catch (Exception e) {
                log.warn("No ACTIVE plan found for account: {}", account.getId());
                continue;
            }

            //calculate phase progress WHEN phase is done(after deadline & phase endDate = LocalDate.now) AND phase successRate==null
            for (Phase phase : plan.getPhases()) {
                processPhaseIfDeadlinePassed(phase, account.getId(), now, deadline);
            }

            //if all phases success rate !null => process calculate plan
            if (allPhasesHaveCompleted(plan)) {
                processPlanCompletion(plan, account);
            }

            processFullyReported(plan, account.getId());
        }
    }

    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void checkPendingPlans() {
        planService.dailyCheckingPlanStatus();
        phaseService.dailyCheckingPhaseStatus();
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
            long totalDays = ChronoUnit.DAYS.between(phase.getStartDate(), phase.getEndDate()) + 1;
            if (!phaseService.isPhaseFullyReported(totalDays, records)) {
                return false;
            }
        }
        return true;
    }

    private void processPhaseIfDeadlinePassed(Phase phase, String accountId, LocalDateTime now, LocalTime deadline) {
        LocalDateTime phaseDeadline = LocalDateTime.of(phase.getEndDate(), deadline);
        if (now.isAfter(phaseDeadline) && phase.getSuccessRate() == null) {
            List<RecordHabit> recordHabits = recordHabitService.findAllByAccountIdAndDateBetweenAndIsDeletedFalse(
                    accountId, phase.getStartDate(), phase.getEndDate()
            );
            log.info("found phases");
            phaseService.calculateSuccessRateAndUpdatePhase(phase, recordHabits);
        }
    }

    private void processPlanCompletion(Plan plan, Account account) {
        int totalPhases = plan.getPhases().size();
        int successCount = 0;
        int maxCig = 0;
        Integer minCig = null;
        long totalReportedDays = 0;
        long totalNotReportedDays = 0;

        for (Phase phase : plan.getPhases()) {
            if (phase.getPhaseStatus().equals(PhaseStatus.SUCCESS)) {
                successCount++;
            }

            int phaseMax = phase.getMostSmokeCig();
            if (phaseMax > maxCig) {
                maxCig = phaseMax;
            }

            int phaseMin = phase.getLeastSmokeCig();
            if (minCig == null || phaseMin < minCig) {
                minCig = phaseMin;
            }

            totalReportedDays += phase.getTotalDaysReported();
            totalNotReportedDays += phase.getTotalDaysNotReported();
        }

        double successRatio = (double) successCount / totalPhases;
        PlanStatus planStatus = (successCount > totalPhases / 2) ? PlanStatus.COMPLETE : PlanStatus.FAILED;
        plan.setPlanStatus(planStatus);
        plan.setTotalMostSmoked(maxCig);
        plan.setTotalLeastSmoked(minCig != null ? minCig : 0);
        plan.setTotalDaysReported(totalReportedDays);
        plan.setTotalDaysNotReported(totalNotReportedDays);

        if (planStatus == PlanStatus.COMPLETE) {
            log.info("score bonus for planSuccess:");
            scoreService.updateScore(account.getId(), ScoreRule.PLAN_SUCCESS);
        }

        planService.updateCompletedPlan(plan, successRatio * 100.0, plan.getPlanStatus());
        if (account.getStatus() == AccountStatus.ONLINE) {
            notificationService.sendPlanDoneNotification(plan.getPlanName(), account.getId());
        } else {
            mailService.sendPlanSummary(plan.getPlanName(), plan.getStartDate(), plan.getEndDate(), totalReportedDays, totalNotReportedDays, maxCig, minCig, account.getId(), plan.getPlanStatus(), plan.getSuccessRate());
        }
    }

    private void processFullyReported(Plan plan, String accountId) {
        if ((plan.getPlanStatus() == PlanStatus.COMPLETE || plan.getPlanStatus() == PlanStatus.FAILED)
                && allPhasesFullyReported(plan, accountId)
        ) {
            log.info("earned REPORT_ALL_PLAN score");
            scoreService.updateScore(accountId, ScoreRule.REPORT_ALL_PLAN);
        }
    }
}