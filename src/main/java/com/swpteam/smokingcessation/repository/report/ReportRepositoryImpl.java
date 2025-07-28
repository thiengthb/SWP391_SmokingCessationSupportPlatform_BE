package com.swpteam.smokingcessation.repository.report;


import com.swpteam.smokingcessation.domain.dto.report.*;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReportRepositoryImpl implements IReportRepository {

    private final EntityManager entityManager;

    @Override
    public ReportSummaryResponse getReportSummary(LocalDateTime from, LocalDateTime to) {
        Object[] result = (Object[]) entityManager.createQuery("""
                        SELECT
                            (SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.createdAt BETWEEN :from AND :to AND t.isDeleted = false),
                            (SELECT COUNT(a) FROM Account a WHERE a.createdAt BETWEEN :from AND :to AND a.isDeleted = false),
                            (SELECT COUNT(a) FROM Account a WHERE a.isDeleted = false),
                            (SELECT COUNT(a) FROM Account a WHERE a.updatedAt BETWEEN :from AND :to AND a.isDeleted = false)
                        """)
                .setParameter("from", from)
                .setParameter("to", to)
                .getSingleResult();

        return ReportSummaryResponse.builder()
                .revenue((Double) result[0])
                .newAccounts(((Long) result[1]).intValue())
                .currentAccounts(((Long) result[2]).intValue())
                .activeAccounts(((Long) result[3]).intValue())
                .fromDate(from)
                .toDate(to)
                .build();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<UserActivityResponse> getUserActivity(LocalDateTime from, LocalDateTime to) {
        List<Object[]> results = entityManager.createNativeQuery("""
                        SELECT
                            DATE(a.created_at) as date,
                            COUNT(CASE WHEN a.created_at BETWEEN :from AND :to THEN 1 END) AS new_accounts
                        FROM account a
                        WHERE a.created_at BETWEEN :from AND :to AND a.is_deleted = false
                        GROUP BY DATE(a.created_at)
                        ORDER BY DATE(a.created_at)
                        """)
                .setParameter("from", from)
                .setParameter("to", to)
                .getResultList();

        List<UserActivityResponse> reports = new ArrayList<>();

        for (Object[] row : results) {
            reports.add(UserActivityResponse.builder()
                    .date(((java.sql.Date) row[0]).toLocalDate())
                    .newAccounts(((Number) row[1]).intValue())
                    .build());
        }
        return reports;
    }

    @Override
    public UserDistributionResponse getUserDistribution() {
        Object[] result = (Object[]) entityManager.createNativeQuery("""
                        SELECT
                            COUNT(*) as total_accounts,
                            COUNT(CASE WHEN a.status = 'OFFLINE' THEN 1 END) as offline_accounts,
                            COUNT(CASE WHEN a.status = 'ONLINE' THEN 1 END) as online_accounts,
                            COUNT(CASE WHEN a.status = 'INACTIVE' THEN 1 END) as inactive_accounts
                        FROM account a
                        WHERE a.is_deleted = false
                        """)
                .getSingleResult();

        return UserDistributionResponse.builder()
                .totalAccounts(((Long) result[0]).intValue())
                .offlineAccounts(((Long) result[1]).intValue())
                .onlineAccounts(((Long) result[2]).intValue())
                .inactiveAccounts(((Long) result[3]).intValue())
                .build();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<RevenueResponse> getRevenue(LocalDateTime from, LocalDateTime to) {
        List<Object[]> result = entityManager.createNativeQuery("""
                        SELECT
                            DATE(t.created_at) as date,
                            SUM(t.amount) as revenue
                        FROM transaction t
                        WHERE t.created_at BETWEEN :from AND :to AND t.is_deleted = false
                        GROUP BY DATE(t.created_at)
                        ORDER BY DATE(t.created_at)
                        """)
                .setParameter("from", from)
                .setParameter("to", to)
                .getResultList();

        List<RevenueResponse> revenue = new ArrayList<>();

        for (Object[] row : result) {
            revenue.add(RevenueResponse.builder()
                    .date(((java.sql.Date) row[0]).toLocalDate())
                    .revenue((double) row[1])
                    .build());
        }
        return revenue;
    }

    @Override
    public PremiumDistributionResponse getPremiumDistribution() {
        Object[] result = (Object[]) entityManager.createNativeQuery("""
                        SELECT
                            COUNT(*) as total_accounts,
                            (
                                SELECT COUNT(DISTINCT s.account_id)
                                FROM subscription s
                                WHERE s.is_deleted = false
                            ) as premium_accounts
                        FROM account a
                        WHERE a.is_deleted = false
                        """)
                .getSingleResult();

        return PremiumDistributionResponse.builder()
                .totalAccounts(((Long) result[0]).intValue())
                .premiumAccounts(((Long) result[1]).intValue())
                .nonPremiumAccounts(((Long) result[0]).intValue() - ((Long) result[1]).intValue())
                .build();
    }
}
