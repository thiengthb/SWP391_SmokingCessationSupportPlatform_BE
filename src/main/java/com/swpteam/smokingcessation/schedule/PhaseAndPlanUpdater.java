package com.swpteam.smokingcessation.schedule;

import com.swpteam.smokingcessation.domain.entity.*;
import com.swpteam.smokingcessation.domain.enums.PhaseStatus;
import com.swpteam.smokingcessation.domain.enums.PlanStatus;
import com.swpteam.smokingcessation.domain.enums.ScoreRule;
import com.swpteam.smokingcessation.service.interfaces.notification.INotificationService;
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
    INotificationService notificationService;

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
                log.info("found active plan with accId:{}", plan.getAccount().getId());
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

        // DUYỆT 1 LẦN QUA TẤT CẢ PHASE
        for (Phase phase : plan.getPhases()) {
            // Đếm số phase thành công
            if (phase.getPhaseStatus().equals(PhaseStatus.SUCCESS)) {
                successCount++;
            }

            // Tìm max thuốc
            Integer phaseMax = phase.getMostSmokeCig();
            if (phaseMax != null && phaseMax > maxCig) {
                maxCig = phaseMax;
            }

            // Tìm min thuốc
            Integer phaseMin = phase.getLeastSmokeCig();
            if (phaseMin != null) {
                if (minCig == null || phaseMin < minCig) {
                    minCig = phaseMin;
                }
            }

            // Tính tổng ngày báo cáo và chưa báo cáo
            totalReportedDays += phase.getTotalDaysReported();
            totalNotReportedDays += phase.getTotalDaysNotReported();
        }

        // Cập nhật thông tin plan
        double successRatio = (double) successCount / totalPhases;
        PlanStatus planStatus = (successCount > totalPhases / 2) ? PlanStatus.COMPLETE : PlanStatus.FAILED;
        plan.setPlanStatus(planStatus);
        plan.setTotalMostSmoked(maxCig);
        plan.setTotalLeastSmoked(minCig != null ? minCig : 0);
        plan.setTotalDaysReported(totalReportedDays);
        plan.setTotalDaysNotReported(totalNotReportedDays);

        // Cộng điểm nếu thành công
        if (planStatus == PlanStatus.COMPLETE) {
            log.info("score bonus for planSuccess:");
            scoreService.updateScore(account.getId(), ScoreRule.PLAN_SUCCESS);
        }

        // Cập nhật DB và gửi thông báo
        planService.updateCompletedPlan(plan, successRatio * 100.0, plan.getPlanStatus());
        notificationService.sendPlanDoneNotification(plan.getPlanName(), account.getId());
    }

    private void processFullyReported(Plan plan, String accountId) {
        if (allPhasesFullyReported(plan, accountId)) {
            log.info("earned REPORT_ALL_PLAN score");
            scoreService.updateScore(accountId, ScoreRule.REPORT_ALL_PLAN);
        }
    }
}