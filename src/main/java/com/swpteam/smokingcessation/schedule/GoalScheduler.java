package com.swpteam.smokingcessation.schedule;

import com.swpteam.smokingcessation.domain.entity.Account;
import com.swpteam.smokingcessation.domain.entity.Goal;
import com.swpteam.smokingcessation.domain.enums.ScoreRule;
import com.swpteam.smokingcessation.repository.AccountRepository;
import com.swpteam.smokingcessation.repository.GoalRepository;
import com.swpteam.smokingcessation.service.impl.profile.GoalProgressServiceImpl;
import com.swpteam.smokingcessation.service.impl.tracking.ScoreServiceImpl;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GoalScheduler {

    GoalRepository goalRepository;
    AccountRepository accountRepository;
    GoalProgressServiceImpl goalProgressService;
    ScoreServiceImpl scoreService;

    @Transactional
    @Scheduled(cron = "0 0 22 * * *")
    public void checkGoalCompletion() {
        List<Account> accounts = accountRepository.findAllByIsDeletedFalse();

        for (Account account : accounts) {
            List<Goal> goals = goalRepository.findAllActiveGoals(account.getId());

            for (Goal goal : goals) {
                goalProgressService.updateProgress(goal, account);
                boolean completed = goalProgressService.checkGoalForAccount(goal, account);
                if (completed) {
                    goalProgressService.markAsCompleted(goal, account);
                    log.info("achievement score earn:");
                    scoreService.updateScore(account.getId(), ScoreRule.ACHIEVEMENT_EARNED);
                }
            }
        }

    }
}
