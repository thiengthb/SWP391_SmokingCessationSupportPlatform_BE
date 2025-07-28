package com.swpteam.smokingcessation.repository.report;

import com.swpteam.smokingcessation.domain.dto.statistics.AdminStatisticResponse;
import com.swpteam.smokingcessation.domain.dto.statistics.MemberStatisticResponse;
import com.swpteam.smokingcessation.domain.dto.statistics.MembershipRevenue;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class StatisticRepositoryImpl implements IStatisticRepository {

    private final EntityManager entityManager;

    @Override
    public MemberStatisticResponse getMemberStatisticsByAccountId(String accountId) {
        Object[] result = (Object[]) entityManager.createQuery("""
                        SELECT
                            (SELECT COALESCE(SUM(r.cigarettesSmoked), 0) FROM RecordHabit r WHERE r.isDeleted = false AND r.account.id = :accountId),
                            (SELECT COUNT(*) FROM RecordHabit r WHERE r.isDeleted = false AND r.account.id = :accountId),
                            (SELECT COUNT(*) FROM RecordHabit r WHERE r.isDeleted = false AND r.account.id = :accountId)
                        """)
                .setParameter("accountId", accountId)
                .getSingleResult();

        long totalCigarettes = ((Number) result[0]).longValue();
        long daysTracked = ((Number) result[1]).longValue();
        long totalRecords = ((Number) result[2]).longValue();

        double avgPerDay = daysTracked == 0 ? 0.0 : (double) totalCigarettes / daysTracked;

        return MemberStatisticResponse.builder()
                .avgCigarettesPerDay(avgPerDay)
                .daysTracked(daysTracked)
                .totalRecords(totalRecords)
                .build();
    }

    @Override
    public MemberStatisticResponse getCurrentMonthMemberStatistics(String accountId) {
        LocalDate now = LocalDate.now();
        LocalDate firstDayOfMonth = now.withDayOfMonth(1);

        Object[] result = (Object[]) entityManager.createQuery("""
                        SELECT
                            (SELECT COALESCE(SUM(r.cigarettesSmoked), 0) FROM RecordHabit r WHERE r.date BETWEEN :from AND :to AND r.isDeleted = false AND r.account.id = :accountId),
                            (SELECT COUNT(*) FROM RecordHabit r WHERE r.date BETWEEN :from AND :to AND r.isDeleted = false AND r.account.id = :accountId),
                            (SELECT COUNT(*) FROM RecordHabit r WHERE r.date BETWEEN :from AND :to AND r.isDeleted = false AND r.account.id = :accountId)
                        """)
                .setParameter("accountId", accountId)
                .setParameter("from", firstDayOfMonth)
                .setParameter("to", now)
                .getSingleResult();

        long totalCigarettes = ((Number) result[0]).longValue();
        long daysTracked = ((Number) result[1]).longValue();
        long totalRecords = ((Number) result[2]).longValue();

        double avgPerDay = daysTracked == 0 ? 0.0 : (double) totalCigarettes / daysTracked;

        return MemberStatisticResponse.builder()
                .avgCigarettesPerDay(avgPerDay)
                .daysTracked(daysTracked)
                .totalRecords(totalRecords)
                .build();
    }

    @Override
    @SuppressWarnings("unchecked")
    public AdminStatisticResponse getAdminStatistics() {
        Object result = entityManager.createQuery("""
                    SELECT COALESCE(SUM(t.amount), 0)
                        FROM Transaction t
                        WHERE t.isDeleted = false AND t.status = 'COMPLETED'
                """).getSingleResult();

        List<Object[]> membershipResults = entityManager.createQuery("""
                    SELECT m.name, COALESCE(SUM(t.amount), 0)
                        FROM Membership m
                        LEFT JOIN Transaction t ON t.membership.id = m.id AND t.isDeleted = false AND t.status = 'COMPLETED'
                        WHERE m.price != 0
                        GROUP BY m.name
                """).getResultList();

        List<MembershipRevenue> revenueByMembership = membershipResults.stream()
                .map(row -> MembershipRevenue.builder()
                        .name((String) row[0])
                        .membershipRevenue(((Number) row[1]).doubleValue())
                        .build())
                .toList();

        double totalRevenue = ((Number) result).doubleValue();

        return AdminStatisticResponse.builder()
                .totalRevenue(totalRevenue)
                .revenueByMembership(revenueByMembership)
                .build();
    }

    @Override
    @SuppressWarnings("unchecked")
    public AdminStatisticResponse getCurrentMonthAdminStatistics(){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime firstDayOfMonth = now.withDayOfMonth(1);
        Object result = entityManager.createQuery("""
                    SELECT COALESCE(SUM(t.amount), 0)
                    FROM Transaction t
                    WHERE t.isDeleted = false
                    AND t.createdAt BETWEEN :from AND :to
                    AND t.status = 'COMPLETED'
                """)
                .setParameter("from", firstDayOfMonth)
                .setParameter("to", now)
                .getSingleResult();

        List<Object[]> membershipResults = entityManager.createQuery("""
                    SELECT m.name, COALESCE(SUM(t.amount), 0)
                                FROM Membership m
                                LEFT JOIN Transaction t ON t.membership.id = m.id AND t.isDeleted = false AND t.status = 'COMPLETED' AND t.createdAt BETWEEN :from AND :to
                                WHERE m.price != 0
                                GROUP BY m.name
                """)
                .setParameter("from", firstDayOfMonth)
                .setParameter("to", now)
                .getResultList();

        List<MembershipRevenue> revenueByMembership = membershipResults.stream()
                .map(row -> MembershipRevenue.builder()
                        .name((String) row[0])
                        .membershipRevenue(((Number) row[1]).doubleValue())
                        .build())
                .toList();

        double totalRevenue = ((Number) result).doubleValue();

        return AdminStatisticResponse.builder()
                .totalRevenue(totalRevenue)
                .revenueByMembership(revenueByMembership)
                .build();
    }
}
