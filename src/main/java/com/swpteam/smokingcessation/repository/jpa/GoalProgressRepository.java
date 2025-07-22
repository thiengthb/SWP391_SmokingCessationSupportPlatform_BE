package com.swpteam.smokingcessation.repository.jpa;

import com.swpteam.smokingcessation.domain.entity.GoalProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface GoalProgressRepository extends JpaRepository<GoalProgress, String> {
    GoalProgress findByGoalIdAndAccountId(String planId, String accountId);

    @Query("""
            SELECT s.number
            FROM Streak s
            WHERE s.account.id = :accountId
            """)
    BigDecimal StreakGoalProgression(String accountId);

    @Query(value = """
            SELECT SUM(
                         (h.pack_price / h.cigarettes_per_pack) *
                         (h.cigarettes_per_day - r.cigarettes_smoked)
                     )
            FROM record_habit r
            JOIN health h ON r.account_id = h.account_id
            WHERE r.account_id = :accountId
              AND r.cigarettes_smoked < h.cigarettes_per_day
              AND h.created_at = (
                  SELECT MAX(created_at)
                  FROM health
                  WHERE account_id = :accountId
              )
            """, nativeQuery = true)
    BigDecimal MoneySavedGoalProgression(String accountId);

    @Query("""
            SELECT COUNT(*)
            FROM Plan p
            WHERE p.account.id = :accountId AND p.planStatus = 'COMPLETED'
            """)
    BigDecimal PlanGoalProgression(String accountId);

    @Query("""
            SELECT COUNT(*)
            FROM Phase p1 JOIN Plan p2 ON p1.plan.id = p2.id
            WHERE p2.account.id = :accountId AND p1.phaseStatus = 'COMPLETED'
            """)
    BigDecimal PhaseGoalProgression(String accountId);


    //Cant use JPQL -> return 1 or 0 for MySQL with native query
    //Long data type is converted to boolean manually
    @Query(value = """
            SELECT MAX(streak_length)
            FROM (
                SELECT COUNT(*) AS streak_length
                FROM (
                    SELECT
                        date,
                        ROW_NUMBER() OVER (ORDER BY date) -
                        ROW_NUMBER() OVER (PARTITION BY grp ORDER BY date) AS grp_id
                    FROM(
                        SELECT
                            date,
                            cigarettes_smoked,
                            SUM(CASE WHEN cigarettes_smoked = 0 THEN 0 ELSE 1 END)
                                OVER (ORDER BY date) AS grp
                        FROM record_habit
                        WHERE account_id = :accountId
                    ) t
                    WHERE cigarettes_smoked = 0
                ) streaks_groups
                GROUP BY grp_id
            )streak
            """, nativeQuery = true)
    BigDecimal SmokeFreeGoalProgression(String accountId);

    @Query(value = """
            SELECT MAX(streak_length)
                        FROM (
                            SELECT COUNT(*) AS streak_length
                            FROM (
                                SELECT
                                    end_date,
                                    ROW_NUMBER() OVER (ORDER BY end_date) -
                                    ROW_NUMBER() OVER (PARTITION BY grp ORDER BY end_date) AS grp_id
                                FROM (
                                    SELECT
                                        end_date,
                                        plan_status,
                                        SUM(CASE WHEN plan_status = plan_status THEN 0 ELSE 1 END)
                                            OVER (ORDER BY end_date) as grp
                                    FROM plan
                                    WHERE account_id = :accountId
                                ) t
                                WHERE plan_status = 'COMPLETED'
                            ) streaks_groups
                            GROUP BY grp_id
                        )streak
            """, nativeQuery = true)
    BigDecimal PlanStreakGoalProgression(String accountId);

    @Query(value = """
            SELECT gp.*
                FROM goal_progress gp
                JOIN (
                    SELECT goal_id, MIN(earned_at) as min_earned
                    FROM goal_progress
                    WHERE progress = 1.0
                    GROUP BY goal_id
                ) earliest ON gp.goal_id = earliest.goal_id AND gp.earned_at = earliest.min_earned
                JOIN goal g ON g.id = gp.goal_id
                WHERE g.difficulty = 'MEDAL'
                ORDER BY gp.earned_at ASC
            """, nativeQuery = true)
    List<GoalProgress> findEarliestCompletedProgressPerMedalGoal();
}
