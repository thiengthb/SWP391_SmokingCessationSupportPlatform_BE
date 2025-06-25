package com.swpteam.smokingcessation.schedule;

import com.swpteam.smokingcessation.domain.entity.*;
import com.swpteam.smokingcessation.domain.enums.PlanStatus;
import com.swpteam.smokingcessation.service.impl.report.PlanSummaryHelper;
import com.swpteam.smokingcessation.service.interfaces.profile.ISettingService;
import com.swpteam.smokingcessation.service.interfaces.tracking.IPlanService;
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
public class PlanSummary {

    ISettingService settingService;
    IPlanService planService;
    PlanSummaryHelper planSummaryHelper;
/*
    @Scheduled(cron = "0 * * * * *") // Chạy mỗi phút
    public void generatePlanSummary() {

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

            // Kiểm tra xem plan đã kết thúc chưa
            LocalDateTime planDeadline = LocalDateTime.of(plan.getEndDate(), deadline);
            if (now.isAfter(planDeadline)) {
                planSummaryHelper.markPlanAsCompleteAndGenerateSummary(plan);
            }
        }
    }

 */
}
