package com.swpteam.smokingcessation.repository;

import com.swpteam.smokingcessation.domain.entity.Goal;
import com.swpteam.smokingcessation.domain.entity.GoalProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GoalProgressRepository extends JpaRepository<GoalProgress, String> {
    GoalProgress findByGoalIdAndAccountId(String planId, String accountId);
}
