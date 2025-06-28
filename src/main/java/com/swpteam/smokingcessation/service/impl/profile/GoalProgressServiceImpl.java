package com.swpteam.smokingcessation.service.impl.profile;

import com.swpteam.smokingcessation.domain.entity.Account;
import com.swpteam.smokingcessation.domain.entity.Goal;
import com.swpteam.smokingcessation.domain.entity.GoalProgress;
import com.swpteam.smokingcessation.repository.AccountRepository;
import com.swpteam.smokingcessation.repository.GoalProgressRepository;
import com.swpteam.smokingcessation.repository.GoalRepository;
import com.swpteam.smokingcessation.service.interfaces.profile.IGoalProgressService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class GoalProgressServiceImpl implements IGoalProgressService {

    GoalProgressRepository goalProgressRepository;
    GoalRepository goalRepository;
    AccountRepository accountRepository;

    @Override
    public void createGoalProgress(Goal goal, Account account) {
        goalProgressRepository.save(GoalProgress.builder()
                .goal(goal)
                .progress(BigDecimal.ZERO)
                .account(account)
                .build());
    }

    @Override
    public boolean checkGoalForAccount(Goal goal, Account account) {
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

    @Override
    @Transactional
    public void markAsCompleted(Goal goal, Account account) {
        GoalProgress goalProgress = goalProgressRepository.findByGoalIdAndAccountId(goal.getId(), account.getId());

        goalProgress.setEarnedAt(LocalDateTime.now());

        goalProgressRepository.save(goalProgress);
    }

    @Override
    @Transactional
    public void updateProgress(Goal goal, Account account) {
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
            BigDecimal percentage = progress.divide(goalValue);
            goalProgress.setProgress(percentage);
        } else {
            goalProgress.setProgress(BigDecimal.ZERO);
        }

        goalProgressRepository.save(goalProgress);
    }

    @Override
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

    @Override
    @Transactional
    public void ensureGlobalProgressForNewAccount(Account account) {
        List<Goal> globalGoals = goalRepository.findAllByAccountIsNullAndIsDeletedFalse();

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
