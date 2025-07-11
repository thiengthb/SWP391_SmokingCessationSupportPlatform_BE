package com.swpteam.smokingcessation.feature.version1.profile.service;

import com.swpteam.smokingcessation.domain.entity.Account;
import com.swpteam.smokingcessation.domain.entity.Goal;

public interface IGoalProgressService {
    void createGoalProgress(Goal goal, Account account);

    boolean checkGoalForAccount(Goal goal, Account account);

    void markAsCompleted(Goal goal, Account account);

    void updateProgress(Goal goal, Account account);

    void ensureGlobalGoalProgressForAllAccounts();

    void ensureGlobalProgressForNewAccount(Account account);
}
