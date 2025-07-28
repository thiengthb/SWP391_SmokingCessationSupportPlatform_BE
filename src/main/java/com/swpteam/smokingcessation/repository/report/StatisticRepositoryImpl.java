package com.swpteam.smokingcessation.repository.report;

import com.swpteam.smokingcessation.domain.dto.statistics.MemberStatisticResponse;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

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
}
