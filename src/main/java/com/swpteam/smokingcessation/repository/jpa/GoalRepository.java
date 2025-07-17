package com.swpteam.smokingcessation.repository.jpa;

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
}

