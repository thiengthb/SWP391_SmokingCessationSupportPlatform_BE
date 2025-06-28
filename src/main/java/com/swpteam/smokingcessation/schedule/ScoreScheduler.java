package com.swpteam.smokingcessation.schedule;


import com.swpteam.smokingcessation.domain.entity.Member;
import com.swpteam.smokingcessation.domain.entity.RecordHabit;
import com.swpteam.smokingcessation.domain.enums.ScoreRule;
import com.swpteam.smokingcessation.service.interfaces.profile.IMemberService;
import com.swpteam.smokingcessation.service.interfaces.profile.IScoreService;
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

    IRecordHabitService recordHabitService;
    IScoreService scoreService;
    IMemberService memberService;

    @Scheduled(cron = "*/20 * * * * *")
    //day 26
    public void scoringCalculate() {
        LocalDate yesterday = LocalDate.now().minusDays(1); // day 25 -ngày cần chấm điểm

        List<Member> members = memberService.findAllMember();

        for (Member member : members) {
            String accountId = member.getAccount().getId();
            log.info("account id: {} ", accountId);

            List<RecordHabit> records = recordHabitService.getAllRecordNoSmoke(accountId);

            int noSmokeDays = records.size();

            switch (noSmokeDays) {
                case 10:
                    log.info("no smoking for 10 days reward");
                    scoreService.updateScore(accountId, ScoreRule.NO_SMOKING_FOR_10DAYS);
                    break;
                case 30:
                    log.info("no smoking for 30 days reward");
                    scoreService.updateScore(accountId, ScoreRule.NO_SMOKING_FOR_30DAYS);
                    break;
                case 180:
                    log.info("no smoking for 180 days reward");
                    scoreService.updateScore(accountId, ScoreRule.NO_SMOKING_FOR_180DAYS);
                    break;
                case 365:
                    log.info("no smoking for 365 days reward");
                    scoreService.updateScore(accountId, ScoreRule.NO_SMOKING_FOR_365DAYS);
                    break;
            }

            Optional<RecordHabit> recordY = recordHabitService.getRecordByDate(accountId, yesterday);
            int cigCheckDay;
            if (recordY.isPresent()) {
                cigCheckDay = recordY.get().getCigarettesSmoked();
                log.info("checkDay cigarettes smoked [{}]", cigCheckDay);
                if (cigCheckDay == 0) {
                    log.info("add score for no smoke day");
                    scoreService.updateScore(accountId, ScoreRule.NO_SMOKE);
                }
            } else {
                continue;
            }

            // day24/23,...
            Optional<RecordHabit> recordBeforeY = recordHabitService.getLatestRecordBeforeDate(accountId, yesterday);
            if (recordBeforeY.isEmpty()) continue;
            ;
            int cigBefore = recordBeforeY.get().getCigarettesSmoked();
            log.info("Previous cigarettes smoked [{}]", cigBefore);

            if (cigCheckDay <= cigBefore) {
                scoreService.updateScore(accountId, ScoreRule.REDUCE_SMOKE);
                log.info("add score for smoking less than previous");
            } else {
                scoreService.updateScore(accountId, ScoreRule.INCREASE_SMOKE);
                log.info("add score for smoking more than previous");
            }

        }
    }
}
