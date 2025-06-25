package com.swpteam.smokingcessation.schedule;

import com.swpteam.smokingcessation.domain.entity.Account;
import com.swpteam.smokingcessation.domain.entity.Goal;
import com.swpteam.smokingcessation.domain.entity.GoalProgress;
import com.swpteam.smokingcessation.repository.AccountRepository;
import com.swpteam.smokingcessation.repository.GoalProgressRepository;
import com.swpteam.smokingcessation.repository.GoalRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GoalScheduler {

    GoalRepository goalRepository;
    AccountRepository accountRepository;
    GoalProgressRepository goalProgressRepository;

    @Transactional
    @Scheduled(cron = "1 * * * * *")
    public void checkGoalCompletion() {
        ensureGlobalGoalProgressForAllAccounts();
        List<Account> accounts = accountRepository.findAllByIsDeletedFalse();

        for (Account account : accounts) {
            List<Goal> goals = goalRepository.findAllActiveGoals(account.getId());

            for (Goal goal : goals) {
                updateProgress(goal, account);
                boolean completed = checkGoalForAccount(goal, account);
                if (completed) {
                    markAsCompleted(goal, account);
                }
            }
        }

    }


    private boolean checkGoalForAccount(Goal goal, Account account) {
        String accountId = account.getId();
        BigDecimal goalValue = BigDecimal.valueOf(goal.getCriteriaValue());

        return switch (goal.getCriteriaType()) {
            case STREAK -> goalRepository.StreakGoalProgression(accountId).compareTo(goalValue) >= 0;
            case SMOKE_FREE -> goalRepository.SmokeFreeGoalProgression(accountId).compareTo(goalValue) >= 0;
            case MONEY_SAVED -> goalRepository.MoneySavedGoalProgression(accountId).compareTo(goalValue) >= 0;
            case PLAN_STREAK -> goalRepository.PlanStreakGoalProgression(accountId).compareTo(goalValue) >= 0;
            case PLAN_COMPLETE -> goalRepository.PlanGoalProgression(accountId).compareTo(goalValue) >= 0;
            case PHASE_COMPLETE -> goalRepository.PhaseGoalProgression(accountId).compareTo(goalValue) >= 0;
        };
    }

    @Transactional
    private void markAsCompleted(Goal goal, Account account) {
        GoalProgress goalProgress = goalProgressRepository.findByGoalIdAndAccountId(goal.getId(), account.getId());
        goalProgress.setEarnedAt(LocalDateTime.now());
    }

    @Transactional
    private void updateProgress(Goal goal, Account account) {
        GoalProgress goalProgress = goalProgressRepository.findByGoalIdAndAccountId(goal.getId(), account.getId());
        String accountId = account.getId();
        BigDecimal goalValue = BigDecimal.valueOf(goal.getCriteriaValue());

        BigDecimal progress = switch (goal.getCriteriaType()) {
            case STREAK -> goalRepository.StreakGoalProgression(accountId);
            case SMOKE_FREE -> goalRepository.SmokeFreeGoalProgression(accountId);
            case MONEY_SAVED -> goalRepository.MoneySavedGoalProgression(accountId);
            case PLAN_STREAK -> goalRepository.PlanStreakGoalProgression(accountId);
            case PLAN_COMPLETE -> goalRepository.PlanGoalProgression(accountId);
            case PHASE_COMPLETE -> goalRepository.PhaseGoalProgression(accountId);
        };

        if (progress != null && goalValue.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal percentage = progress.divide(goalValue, 1, RoundingMode.DOWN);
            goalProgress.setProgress(percentage);
        } else {
            goalProgress.setProgress(BigDecimal.ZERO);
        }
    }

    @Transactional
    public void ensureGlobalGoalProgressForAllAccounts() {
        List<Account> accounts = accountRepository.findAllByIsDeletedFalse();
        List<Goal> globalGoals = goalRepository.findAllByAccountIsNullAndIsDeletedFalse();

        for (Account account : accounts) {
            for (Goal goal : globalGoals) {
                GoalProgress existing = goalProgressRepository.findByGoalIdAndAccountId(goal.getId(), account.getId());
                if (existing == null) {
                    GoalProgress gp = GoalProgress.builder()
                            .goal(goal)
                            .account(account)
                            .progress(BigDecimal.ZERO)
                            .build();
                    goalProgressRepository.save(gp);
                }
            }
        }
    }
}
