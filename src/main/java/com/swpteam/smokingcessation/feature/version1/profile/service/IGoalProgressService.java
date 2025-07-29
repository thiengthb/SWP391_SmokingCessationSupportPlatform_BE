package com.swpteam.smokingcessation.feature.version1.profile.service;

import com.swpteam.smokingcessation.domain.dto.goal.HallOfFameResponse;
import com.swpteam.smokingcessation.domain.entity.Account;
import com.swpteam.smokingcessation.domain.entity.Goal;

import java.util.List;

public interface IGoalProgressService {
    List<HallOfFameResponse> getHallOfFame();

    void createGoalProgress(Goal goal, Account account);

    boolean checkGoalForAccount(Goal goal, Account account);

    void markAsCompleted(Goal goal, Account account);

    void updateProgress(Goal goal, Account account);

    void ensureGlobalGoalProgressForAllAccounts();

    void ensureGlobalProgressForNewAccount(Account account);
}
