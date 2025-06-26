package com.swpteam.smokingcessation.repository;

import com.swpteam.smokingcessation.domain.entity.Goal;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface GoalRepository extends JpaRepository<Goal, String> {

    Optional<Goal> findByIdAndIsDeletedFalse(String id);

    Optional<Goal> findByNameAndIsDeletedFalse(String name);

    List<Goal> findAllByAccountIsNullAndIsDeletedFalse(Sort sort);

    List<Goal> findAllByAccountIsNullAndIsDeletedFalse();

    List<Goal> findAllByAccountIdAndIsDeletedFalse(String accountId);

    @Query(value = """
                SELECT g.id, g.name, g.icon_url, g.description, g.criteria_type, g.criteria_value, g.created_at, g.updated_at, gp_latest.progress
                FROM goal g
                LEFT JOIN (
                    SELECT gp1.goal_id, gp1.progress, gp1.account_id
                    FROM goal_progress gp1
                    LEFT JOIN (
                        SELECT goal_id, MAX(earned_at) AS latest_ts
                        FROM goal_progress
                        WHERE account_id = :accountId
                        GROUP BY goal_id
                    ) latest_gp
                    ON gp1.goal_id = latest_gp.goal_id AND gp1.earned_at = latest_gp.latest_ts
                    WHERE gp1.account_id = :accountId
                ) gp_latest ON g.id = gp_latest.goal_id
                WHERE g.account_id IS NULL AND g.is_deleted = false
                ORDER BY
                    CASE
                        WHEN gp_latest.progress IS NULL THEN 0
                        ELSE 1
                    END,
                    gp_latest.progress ASC,
                    g.criteria_type ASC,
                    g.criteria_value ASC
            """, nativeQuery = true)
    List<Object[]> findSortedPublicGoals(@Param("accountId") String accountId);

    @Query(value = """
            SELECT g.id, g.name, g.icon_url, g.description, g.criteria_type, g.criteria_value, g.created_at, g.updated_at, gp_latest.progress
            FROM goal g
            LEFT JOIN (
                SELECT gp1.goal_id, gp1.progress, gp1.account_id
                FROM goal_progress gp1
                LEFT JOIN (
                    SELECT goal_id, MAX(earned_at) AS latest_ts
                    FROM goal_progress
                    WHERE account_id = :accountId
                    GROUP BY goal_id
                ) latest_gp
                ON gp1.goal_id = latest_gp.goal_id AND gp1.earned_at = latest_gp.latest_ts
                WHERE gp1.account_id = :accountId
            ) gp_latest ON g.id = gp_latest.goal_id
            WHERE g.account_id = :accountId AND g.is_deleted = false
            ORDER BY
                CASE
                    WHEN gp_latest.progress IS NULL THEN 0
                    ELSE 1
                END,
                gp_latest.progress ASC,
                g.criteria_type ASC,
                g.criteria_value ASC
            """, nativeQuery = true)
    List<Object[]> findSortedPersonalGoals(@Param("accountId") String accountId);

    @Query("""
                SELECT g FROM Goal g JOIN g.goalProgresses gp
                WHERE g.isDeleted = false
                  AND (g.account IS NULL OR g.account.id = :accountId)
                  AND gp.earnedAt IS NULL
            """)
    List<Goal> findAllActiveGoals(String accountId);

    boolean existsByNameAndAccountIdAndIsDeletedFalse(String name, String accountId);

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
}

