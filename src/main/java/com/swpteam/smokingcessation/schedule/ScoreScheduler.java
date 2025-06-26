package com.swpteam.smokingcessation.schedule;

import com.swpteam.smokingcessation.domain.entity.Account;
import com.swpteam.smokingcessation.domain.entity.Plan;
import com.swpteam.smokingcessation.domain.entity.RecordHabit;
import com.swpteam.smokingcessation.domain.enums.ScoreRule;
import com.swpteam.smokingcessation.service.interfaces.profile.IScoreService;
import com.swpteam.smokingcessation.service.interfaces.tracking.IPlanService;
import com.swpteam.smokingcessation.service.interfaces.tracking.IRecordHabitService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ScoreScheduler {

    IPlanService planService;
    IRecordHabitService recordHabitService;
    IScoreService scoreService;

    @Scheduled(cron = "0 0 0 * * *")
    //day 26
    public void scoringCalculate() {
        LocalDate yesterday = LocalDate.now().minusDays(1); // day 25 -ngày cần chấm điểm

        List<Plan> activePlans = planService.getAllActivePlans();

        for (Plan plan : activePlans) {
            Account account = plan.getAccount();
            String accountId = account.getId();

            Optional<RecordHabit> recordY = recordHabitService.getRecordByDate(accountId, yesterday);
            if (recordY.isEmpty()) continue;

            int cigY = recordY.get().getCigarettesSmoked();

            // day24/23,...
            Optional<RecordHabit> recordBeforeY = recordHabitService.getLatestRecordBeforeDate(accountId, yesterday);
            if (recordBeforeY.isEmpty()) continue;
            ;
            int cigBefore = recordBeforeY.get().getCigarettesSmoked();

            if (cigY == 0) {
                scoreService.updateScore(accountId, ScoreRule.NO_SMOKE);
            } else if (cigY <= cigBefore) {
                scoreService.updateScore(accountId, ScoreRule.REDUCE_SMOKE);
            } else {
                scoreService.updateScore(accountId, ScoreRule.INCREASE_SMOKE);
            }

        }
    }
}
