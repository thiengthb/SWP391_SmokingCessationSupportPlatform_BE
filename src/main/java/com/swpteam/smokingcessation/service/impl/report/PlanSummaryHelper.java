package com.swpteam.smokingcessation.service.impl.report;

import com.swpteam.smokingcessation.constant.ErrorCode;
import com.swpteam.smokingcessation.domain.dto.report.PlanSummaryResponse;
import com.swpteam.smokingcessation.domain.entity.*;
import com.swpteam.smokingcessation.domain.enums.AccountStatus;
import com.swpteam.smokingcessation.domain.enums.PlanStatus;
import com.swpteam.smokingcessation.exception.AppException;
import com.swpteam.smokingcessation.integration.mail.MailServiceImpl;
import com.swpteam.smokingcessation.repository.PlanRepository;
import com.swpteam.smokingcessation.service.interfaces.tracking.IPhaseService;
import com.swpteam.smokingcessation.service.interfaces.tracking.IRecordHabitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PlanSummaryHelper {
/*
private final PlanRepository planRepository;
private final IPhaseService phaseService;
private final IRecordHabitService recordHabitService;
private final MailServiceImpl mailService;

@Transactional
public void markPlanAsCompleteAndGenerateSummary(Plan plan) {
    plan.setPlanStatus(PlanStatus.COMPLETE);
    planRepository.save(plan);

    generateSummary(plan);
}

private void generateSummary(Plan plan) {
    LocalDate start = plan.getStartDate();
    LocalDate end = plan.getEndDate();
    Long totalDays = ChronoUnit.DAYS.between(start, end) + 1;

    List<RecordHabit> recordHabits = recordHabitService.findAllByAccountIdAndDateBetweenAndIsDeletedFalse(
            plan.getAccount().getId(), start, end
    );

    Long daysReported = (long) recordHabits.size();
    Long daysNotReport = totalDays - daysReported;

    LocalDate mostSmokedDay = recordHabits.stream()
            .max(Comparator.comparingInt(RecordHabit::getCigarettesSmoked))
            .orElseThrow(() -> new AppException(ErrorCode.RECORD_NOT_FOUND))
            .getDate();

    LocalDate leastSmokeDay = recordHabits.stream()
            .min(Comparator.comparingInt(RecordHabit::getCigarettesSmoked))
            .orElseThrow(() -> new AppException(ErrorCode.RECORD_NOT_FOUND))
            .getDate();

    double totalPhaseRate = 0;
    int successPhaseCount = 0;

    for (Phase phase : plan.getPhases()) {
        double phaseRate = phaseService.calculateSuccessRateAndUpdatePhase(phase, recordHabits);
        totalPhaseRate += phaseRate;

        if (phaseRate >= 50.0) {
            successPhaseCount++;
        }
    }

    int phaseCount = plan.getPhases().size();
    double planSuccessRate = (phaseCount == 0) ? 0 : totalPhaseRate / phaseCount;

    PlanSummaryResponse response = PlanSummaryResponse.builder()
            .accountId(plan.getAccount().getId().toString())
            .username(plan.getAccount().getUsername())
            .email(plan.getAccount().getEmail())
            .successRate(String.format("%.2f", planSuccessRate))
            .leastSmokeDay(leastSmokeDay.toString())
            .mostSmokeDay(mostSmokedDay.toString())
            .reportedDays(daysReported)
            .missedDays(daysNotReport)
            .planStartDate(start.toString())
            .planEndDate(end.toString())
            .build();

    if (plan.getAccount().getStatus().equals(AccountStatus.OFFLINE)) {
        mailService.sendPlanSummaryEmail(plan.getAccount(), response);
    }
    log.info("Plan summary for user {} - successRate: {}%, reportedDays: {}, missedDays: {}, best: {}, worst: {}",
            plan.getAccount().getId(),
            planSuccessRate,
            daysReported,
            daysNotReport,
            leastSmokeDay,
            mostSmokedDay
    );
}

 */
}
